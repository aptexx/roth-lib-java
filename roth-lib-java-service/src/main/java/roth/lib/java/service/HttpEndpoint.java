package roth.lib.java.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import roth.lib.java.Characters;
import roth.lib.java.form.FormMapper;
import roth.lib.java.form.MultipartFormMapper;
import roth.lib.java.http.HttpMethod;
import roth.lib.java.http.HttpUrl;
import roth.lib.java.lang.List;
import roth.lib.java.lang.Map;
import roth.lib.java.mapper.Mapper;
import roth.lib.java.mapper.MapperConfig;
import roth.lib.java.mapper.MapperType;
import roth.lib.java.reflector.EntityReflector;
import roth.lib.java.reflector.MapperReflector;
import roth.lib.java.reflector.PropertyReflector;
import roth.lib.java.service.annotation.Service;
import roth.lib.java.service.reflector.MethodReflector;
import roth.lib.java.service.reflector.ServiceReflector;
import roth.lib.java.type.MimeType;
import roth.lib.java.util.EnumUtil;
import roth.lib.java.util.ReflectionUtil;
import roth.lib.java.validate.Validator;
import roth.lib.java.validate.ValidatorException;

@SuppressWarnings("serial")
public abstract class HttpEndpoint extends HttpServlet implements Characters
{
	protected static String ORIGIN 								= "Origin";
	protected static String ANY 								= "*";
	protected static String X_CSRF_TOKEN						= "X-Csrf-Token";
	protected static String ACCESS_CONTROL_ALLOW_ORIGIN 		= "Access-Control-Allow-Origin";
	protected static String ACCESS_CONTROL_ALLOW_CREDENTIALS 	= "Access-Control-Allow-Credentials";
	protected static String ACCESS_CONTROL_ALLOW_METHODS 		= "Access-Control-Allow-Methods";
	protected static String ACCESS_CONTROL_EXPOSE_HEADERS 		= "Access-Control-Expose-Headers";
	protected static String CONTENT_TYPE_PARAM	 				= "contentType";
	protected static String CONTENT_TYPE_HEADER 				= "Content-Type";
	protected static String ACCEPT_PARAM		 				= "accept";
	protected static String ACCEPT_HEADER		 				= "Accept";
	protected static String ALLOWED_METHODS 					= "GET, POST";
	protected static List<HttpMethod> SUPPORTED_METHODS			= List.fromArray(HttpMethod.GET, HttpMethod.POST);
	protected static List<String> LOCALHOSTS 					= List.fromArray("localhost", "127.0.0.1");
	protected static String ENDPOINT 							= "_endpoint";
	protected static String SERVICE 							= "service";
	protected static String METHOD 								= "method";
	protected static Pattern SERVICE_METHOD_PATTERN 			= Pattern.compile("(?:^|/)(?<" + SERVICE + ">[\\w\\-]+)/(?<" + METHOD + ">[\\w]+)(?:/|$)");
	protected static Pattern BOUNDARY_PATTERN					= Pattern.compile("boundary\\=(?:\")?(.+?)(?:\"|;|$)");
	protected static String MAX_LENGTH_ERROR 					= "%d exceeds max length of %d characters";
	
	protected static Map<String, ServiceReflector> serviceReflectorMap = new Map<String, ServiceReflector>();
	
	protected MapperReflector mapperReflector = MapperReflector.get();
	protected MapperConfig mapperConfig = MapperConfig.get();
	
	public static void register(Class<? extends HttpService> serviceClass)
	{
		Service service = serviceClass.getDeclaredAnnotation(Service.class);
		if(service != null)
		{
			String serviceName = service.name();
			if(serviceName != null && !serviceName.isEmpty())
			{
				serviceReflectorMap.put(serviceName, new ServiceReflector(serviceClass, serviceName));
			}
		}
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
	{
		Object methodResponse = null;
		String debugRequest = "";
		List<HttpError> errors = new List<HttpError>();
		MimeType requestContentType = getRequestContentType(request, response);
		MimeType responseContentType = getResponseContentType(request, response);
		Mapper responseMapper = getResponseMapper(request, response, responseContentType);
		response.setHeader(CONTENT_TYPE_HEADER, responseContentType.toString());
		boolean dev = isDev(request, response);
		try
		{
			if(dev || isOriginAllowed(request, response))
			{
				String origin = request.getHeader(ORIGIN);
				if(origin != null)
				{
					response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
					response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());
				}
				else
				{
					response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ANY);
				}
				response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
				response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, X_CSRF_TOKEN);
				HttpMethod httpMethod = HttpMethod.fromString(request.getMethod());
				if(httpMethod != null)
				{
					if(SUPPORTED_METHODS.contains(httpMethod))
					{
						HttpServiceMethod serviceMethod = getServiceMethod(request, response);
						if(serviceMethod != null)
						{
							if(!ENDPOINT.equalsIgnoreCase(serviceMethod.getServiceName()))
							{
								HttpService service = getAnnotatedService(request, response, serviceMethod.getServiceName());
								if(service != null)
								{
									service.setServletContext(request.getServletContext()).setHttpServletRequest(request).setHttpServletResponse(response);
									service.setService(serviceMethod.getServiceName()).setMethod(serviceMethod.getMethodName());
									MethodReflector methodReflector = getMethodReflector(request, response, service.getClass(), serviceMethod.getServiceName(), serviceMethod.getMethodName());
									if(methodReflector != null)
									{
										responseMapper.setContext(methodReflector.getContext());
										responseMapper.setPrettyPrint(methodReflector.isPrettyPrint());
										if(methodReflector.isHttpMethodImplemented(httpMethod))
										{
											boolean hasAjax = methodReflector.isAjax();
											boolean hasApi = methodReflector.isApi();
											boolean ajaxAuthenticated = hasAjax && (!methodReflector.isAuthenticated() || service.isAjaxAuthenticated(methodReflector));
											boolean apiAuthenticated = hasApi && (!methodReflector.isAuthenticated() || service.isApiAuthenticated(methodReflector));
											if(ajaxAuthenticated || apiAuthenticated)
											{
												Object methodRequest = null;
												Mapper requestMapper = null;
												Parameter parameter = methodReflector.getParameter();
												if(parameter != null)
												{
													if(!HttpMethod.GET.equals(httpMethod))
													{
														InputStream input = methodReflector.isGzippedInput() ? new GZIPInputStream(request.getInputStream()) : request.getInputStream();
														service.setRequestContentType(requestContentType);
														requestMapper = getRequestMapper(request, response, requestContentType);
														requestMapper.setContext(methodReflector.getContext()).setContentType(requestContentType);
														if(requestMapper instanceof MultipartFormMapper)
														{
															Matcher matcher = BOUNDARY_PATTERN.matcher(request.getHeader(CONTENT_TYPE_HEADER));
															if(matcher.find())
															{
																((MultipartFormMapper) requestMapper).setBoundary(matcher.group(1));
															}
														}
														service.setRequestMapper(requestMapper);
														methodRequest = requestMapper.deserialize(input, parameter.getParameterizedType());
													}
													else
													{
														requestMapper = getMapperReflector().getMapper(MapperType.FORM, getMapperConfig());
														requestMapper.setContext(methodReflector.getContext()).setContentType(requestContentType);
														if(requestMapper instanceof FormMapper)
														{
															methodRequest = ((FormMapper) requestMapper).deserialize(HttpUrl.parseParamMap(request.getQueryString()), parameter.getParameterizedType());
														}
													}
												}
												if(dev)
												{
													debugRequest = getDebugRequest(request, methodRequest, requestMapper);
												}
												boolean validCsrf = !(ajaxAuthenticated && methodReflector.isAuthenticated() && !service.isValidCsrfToken(methodRequest, dev));
												if(validCsrf)
												{
													boolean authorized = !methodReflector.isAuthenticated() || service.isAuthorized(methodReflector, methodRequest);
													errors.addAll(service.getErrors());
													service.clearErrors();
													if(authorized)
													{
														if(methodRequest != null && requestMapper != null)
														{
															errors.addAll(validate(request, response, methodRequest, requestMapper.getMapperType()));
														}
														if(errors.isEmpty())
														{
															service.setResponseContentType(responseContentType);
															service.setResponseMapper(responseMapper);
															try
															{
																methodResponse = methodReflector.invoke(service, methodRequest);
															}
															catch(InvocationTargetException e)
															{
																if(e.getCause() != null)
																{
																	Throwable cause = e.getCause();
																	HttpErrorType errorType = HttpErrorType.SERVICE_EXCEPTION;
																	if(cause instanceof SQLNonTransientConnectionException || cause instanceof SQLTransientConnectionException)
																	{
																		errorType = HttpErrorType.DATABASE_CONNECTION_EXCEPTION;
																	}
																	errors.add(service.exception(errorType.error(cause), cause));
																}
															}
															errors.addAll(service.getErrors());
															service.clearErrors();
														}
													}
													else
													{
														errors.add(HttpErrorType.SERVICE_NOT_AUTHORIZED.error());
													}
												}
												else
												{
													errors.add(HttpErrorType.SERVICE_CSRF_TOKEN_INVALID.error());
												}
											}
											else
											{
												if(hasAjax && !ajaxAuthenticated)
												{
													errors.add(HttpErrorType.SERVICE_AJAX_NOT_AUTHENTICATED.error());
												}
												else if(hasApi && !apiAuthenticated)
												{
													errors.add(HttpErrorType.SERVICE_API_NOT_AUTHENTICATED.error());
												}
												else
												{
													errors.add(HttpErrorType.SERVICE_CHANNEL_NOT_IMPLEMENTED.error());
												}
											}
										}
										else
										{
											errors.add(HttpErrorType.HTTP_METHOD_NOT_IMPLEMENTED.error());
										}
									}
									else
									{
										errors.add(HttpErrorType.SERVICE_METHOD_MISSING.error());
									}
								}
								else
								{
									errors.add(HttpErrorType.SERVICE_NOT_IMPLEMENTED.error());
								}
							}
							else
							{
								methodResponse = endpoint(request, response, serviceMethod.getMethodName());
							}
						}
						else
						{
							errors.add(HttpErrorType.SERVICE_METHOD_NAME_MISSING.error());
						}
					}
					else
					{
						errors.add(HttpErrorType.HTTP_METHOD_NOT_SUPPORTED.error());
					}
				}	
				else
				{
					errors.add(HttpErrorType.HTTP_METHOD_MISSING.error());
				}
			}
			else
			{
				errors.add(HttpErrorType.HTTP_ORIGIN_UNSUPPORTED.error());
			}
		}
		catch(Throwable e)
		{
			if(dev)
			{
				e.printStackTrace();
			}
			try
			{
				exception(request, response, e);
			}
			catch(Throwable e2)
			{
				e2.printStackTrace();
			}
		}
		if(!errors.isEmpty())
		{
			if(methodResponse == null)
			{
				methodResponse = errorResponse(errors);
			}
			else if(methodResponse instanceof HttpServiceResponse)
			{
				((HttpServiceResponse) methodResponse).setErrors(errors);
			}
		}
		if(methodResponse != null)
		{
			try(OutputStream output = response.getOutputStream())
			{
				responseMapper.serialize(methodResponse, output);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		if(dev)
		{
			System.out.println(debugRequest + getDebugResponse(response, methodResponse, responseMapper));
		}
	}
	
	public abstract void exception(HttpServletRequest request, HttpServletResponse response, Throwable e);
	
	protected Object endpoint(HttpServletRequest request, HttpServletResponse response, String methodName)
	{
		return null;
	}
	
	protected HttpServiceResponse errorResponse(List<HttpError> errors)
	{
		return new HttpServiceResponse().setErrors(errors);
	}
	
	protected boolean isDev(HttpServletRequest request, HttpServletResponse response)
	{
		return getLocalHosts(request, response).contains(request.getServerName());
	}
	
	protected List<String> getLocalHosts(HttpServletRequest request, HttpServletResponse response)
	{
		return LOCALHOSTS;
	}
	
	protected boolean isOriginAllowed(HttpServletRequest request, HttpServletResponse response)
	{
		return true;
	}
	
	protected HttpServiceMethod getServiceMethod(HttpServletRequest request, HttpServletResponse response)
	{
		HttpServiceMethod serviceMethod = null;
		Matcher matcher = SERVICE_METHOD_PATTERN.matcher(request.getPathInfo());
		if(matcher.find())
		{
			serviceMethod = new HttpServiceMethod(matcher.group(SERVICE), matcher.group(METHOD));
		}
		return serviceMethod;
	}
	
	protected final HttpService getAnnotatedService(HttpServletRequest request, HttpServletResponse response, String serviceName)
	{
		HttpService service = null;
		ServiceReflector serviceReflector = serviceReflectorMap.get(serviceName);
		if(serviceReflector != null)
		{
			service = serviceReflector.service();
		}
		if(service == null)
		{
			service = getService(request, response, serviceName);
		}
		return service;
	}
	
	protected HttpService getService(HttpServletRequest request, HttpServletResponse response, String serviceName)
	{
		return null;
	}
	
	protected ServiceReflector getServiceReflector(HttpServletRequest request, HttpServletResponse response, Class<? extends HttpService> serviceClass, String serviceName)
	{
		ServiceReflector serviceReflector = serviceReflectorMap.get(serviceName);
		if(serviceReflector == null)
		{
			serviceReflector = new ServiceReflector(serviceClass, serviceName);
			serviceReflectorMap.put(serviceName, serviceReflector);
		}
		return serviceReflector;
	}
	
	protected MethodReflector getMethodReflector(HttpServletRequest request, HttpServletResponse response, Class<? extends HttpService> serviceClass, String serviceName, String methodName)
	{
		MethodReflector methodReflector = null;
		ServiceReflector serviceReflector = getServiceReflector(request, response, serviceClass, serviceName);
		if(serviceReflector != null)
		{
			methodReflector = serviceReflector.getMethodReflectorMap().get(methodName);
		}
		return methodReflector;
	}
	
	protected MapperReflector getMapperReflector()
	{
		return mapperReflector;
	}
	
	protected MapperConfig getMapperConfig()
	{
		return mapperConfig;
	}
	
	protected MimeType getRequestContentType(HttpServletRequest request, HttpServletResponse response)
	{
		MimeType contentType = MimeType.fromString(HttpUrl.parseParamMap(request.getQueryString()).get(CONTENT_TYPE_PARAM));
		if(contentType == null)
		{
			String contentTypeHeader = request.getHeader(CONTENT_TYPE_HEADER);
			if(contentTypeHeader != null)
			{
				int index = contentTypeHeader.indexOf(";");
				if(index > -1)
				{
					contentTypeHeader = contentTypeHeader.substring(0, index);
				}
				contentType = MimeType.fromString(contentTypeHeader);
				if(contentType != null)
				{
					switch(contentType)
					{
						case APPLICATION_JSON:
						case APPLICATION_XML:
						case APPLICATION_X_WWW_FORM_URLENCODED:
						case MULTIPART_FORM_DATA:
						{
							break;
						}
						case TEXT_PLAIN:
						default:
						{
							contentType = null;
							break;
						}
					}
				}
			}
		}
		return contentType != null ? contentType : MimeType.APPLICATION_JSON;
	}
	
	protected MimeType getResponseContentType(HttpServletRequest request, HttpServletResponse response)
	{
		MimeType contentType = MimeType.fromString(HttpUrl.parseParamMap(request.getQueryString()).get(ACCEPT_PARAM));
		if(contentType == null)
		{
			String acceptHeader = request.getHeader(ACCEPT_HEADER);
			if(acceptHeader != null)
			{
				int index = acceptHeader.indexOf(";");
				if(index > -1)
				{
					acceptHeader = acceptHeader.substring(0, index);
				}
				List<String> acceptTypes = List.fromArray(acceptHeader.split(","));
				accepts: for(String acceptType : acceptTypes)
				{
					contentType = MimeType.fromString(acceptType);
					if(contentType != null)
					{
						switch(contentType)
						{
							case APPLICATION_JSON:
							case APPLICATION_XML:
							{
								break accepts;
							}
							default:
							{
								contentType = null;
								break;
							}
						}
					}
				}
			}
		}
		return contentType != null ? contentType : MimeType.APPLICATION_JSON;
	}
	
	protected Mapper getRequestMapper(HttpServletRequest request, HttpServletResponse response, MimeType contentType)
	{
		Mapper mapper = null;
		switch(contentType)
		{
			case APPLICATION_XML:
			{
				mapper = getMapperReflector().getMapper(MapperType.XML, getMapperConfig());
				break;
			}
			case APPLICATION_X_WWW_FORM_URLENCODED:
			{
				mapper = getMapperReflector().getMapper(MapperType.FORM, getMapperConfig());
				break;
			}
			case MULTIPART_FORM_DATA:
			{
				mapper = getMapperReflector().getMapper(MapperType.MULTIPART_FORM, getMapperConfig());
				break;
			}
			default:
			{
				mapper = getMapperReflector().getMapper(MapperType.JSON, getMapperConfig());
				break;
			}
		}
		return mapper;
	}
	
	protected Mapper getResponseMapper(HttpServletRequest request, HttpServletResponse response, MimeType contentType)
	{
		Mapper mapper = null;
		switch(contentType)
		{
			case APPLICATION_XML:
			{
				mapper = getMapperReflector().getMapper(MapperType.XML, getMapperConfig());
				break;
			}
			default:
			{
				mapper = getMapperReflector().getMapper(MapperType.JSON, getMapperConfig());
				break;
			}
		}
		return mapper;
	}
	
	protected List<HttpError> validate(HttpServletRequest request, HttpServletResponse response, Object value, MapperType mapperType)
	{
		return validate(request, response, value, mapperType, BLANK);
	}
	
	protected List<HttpError> validate(HttpServletRequest request, HttpServletResponse response, Object value, MapperType mapperType, String path)
	{
		List<HttpError> errors = new List<HttpError>();
		if(value != null)
		{
			EntityReflector entityReflector = getMapperReflector().getEntityReflector(value.getClass());
			if(entityReflector != null)
			{
				for(PropertyReflector propertyReflector : entityReflector.getPropertyReflectors(mapperType))
				{
					try
					{
						Type propertyType = propertyReflector.getFieldType();
						String propertyName = propertyReflector.getPropertyName(mapperType);
						Object propertyValue = propertyReflector.getField().get(value);
						if(getMapperReflector().isEntity(propertyType))
						{
							errors.addAll(validate(request, response, propertyValue, mapperType, path + propertyName + DOT));
						}
						else if(ReflectionUtil.isArray(propertyType) || ReflectionUtil.isCollection(propertyType))
						{
							if(propertyValue != null && !byte[].class.isAssignableFrom(propertyReflector.getFieldClass()))
							{
								int i = 0;
								for(Object element : ReflectionUtil.asCollection(propertyValue))
								{
									errors.addAll(validate(request, response, element, mapperType, path + propertyName + LEFT_BRACE + i + RIGHT_BRACE + DOT));
									i++;
								}
							}
						}
						else if(ReflectionUtil.isMap(propertyType))
						{
							if(propertyValue != null)
							{
								for(Entry<?, ?> elementEntry : ReflectionUtil.asMap(propertyValue).entrySet())
								{
									String elementName = null;
									Object elementKey = elementEntry.getKey();
									if(elementKey != null)
									{
										if(elementKey instanceof Enum)
										{
											elementName = EnumUtil.toString((Enum<?>) elementKey);
										}
										else
										{
											elementName = elementKey.toString();
										}
									}
									if(elementName != null)
									{
										errors.addAll(validate(request, response, elementEntry.getValue(), mapperType, path + propertyName + DOT + elementName + DOT));
									}
								}
							}
						}
						else
						{
							boolean blank = propertyValue == null;
							if(!blank && propertyValue instanceof String)
							{
								blank = ((String) propertyValue).isEmpty();
								if(!blank)
								{
									if(propertyReflector.hasTrimLength())
									{
										int trimLength = propertyReflector.getProperty().trimLength();
										if(((String) propertyValue).length() > trimLength)
										{
											propertyValue = ((String) propertyValue).substring(0, trimLength);
											ReflectionUtil.setFieldValue(propertyReflector.getField(), value, propertyValue);
										}
									}
									else if(propertyReflector.hasMaxLength())
									{
										int length = ((String) propertyValue).length();
										int maxLength = propertyReflector.getProperty().maxLength();
										if(length > maxLength)
										{
											errors.add(HttpErrorType.REQUEST_FIELD_INVALID.error().setContext(path + propertyName).setMessage(String.format(MAX_LENGTH_ERROR, length, maxLength)));
										}
									}
								}
							}
							if(!blank)
							{
								for(Validator validator : propertyReflector.getValidators())
								{
									try
									{
										validator.validate(propertyValue, value, entityReflector);
									}
									catch(ValidatorException e)
									{
										errors.add(HttpErrorType.REQUEST_FIELD_INVALID.error().setContext(path + propertyName).setMessage(e.getMessage()));
										break;
									}
								}
							}
							else if(blank && propertyReflector.isRequired())
							{
								errors.add(HttpErrorType.REQUEST_FIELD_REQUIRED.error().setContext(path + propertyName));
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return errors;
	}
	
	protected String getDebugRequest(HttpServletRequest request, Object methodRequest, Mapper mapper)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append("REQUEST");
		builder.append(NEW_LINE);
		builder.append("----------------------------------");
		builder.append(NEW_LINE);
		builder.append(request.getMethod());
		builder.append(SPACE);
		builder.append(request.getRequestURI());
		builder.append(NEW_LINE);
		Enumeration<String> names = request.getHeaderNames();
		while(names.hasMoreElements())
		{
			String name = names.nextElement();
			builder.append(name);
			builder.append(": ");
			builder.append(request.getHeader(name));
			builder.append(NEW_LINE);
		}
		builder.append(NEW_LINE);
		if(methodRequest != null && mapper != null)
		{
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			mapper.setPrettyPrint(true).serialize(methodRequest, output);
			builder.append(output.toString());
			builder.append(NEW_LINE);
			builder.append(NEW_LINE);
		}
		return builder.toString();
	}
	
	protected String getDebugResponse(HttpServletResponse response, Object methodResponse, Mapper mapper)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append("RESPONSE");
		builder.append(NEW_LINE);
		builder.append("----------------------------------");
		builder.append(NEW_LINE);
		builder.append(response.getStatus());
		builder.append(NEW_LINE);
		for(String name : response.getHeaderNames())
		{
			builder.append(name);
			builder.append(": ");
			builder.append(response.getHeader(name));
			builder.append(NEW_LINE);
		}
		builder.append(NEW_LINE);
		if(methodResponse != null && mapper != null)
		{
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			mapper.setPrettyPrint(true).serialize(methodResponse, output);
			builder.append(output.toString());
			builder.append(NEW_LINE);
			builder.append(NEW_LINE);
		}
		return builder.toString();
	}
	
}
