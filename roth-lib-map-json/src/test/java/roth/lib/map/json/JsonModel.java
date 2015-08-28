package roth.lib.map.json;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import roth.lib.annotation.Property;
import roth.lib.map.MapperConfig;
import roth.lib.map.MapperReflector;

public class JsonModel
{
	
	@Property(name = "null_primitive_boolean")
	private boolean nullPrimitiveBoolean;
	
	@Property(name = "null_primitive_byte")
	private byte nullPrimitiveByte;
	
	@Property(name = "null_primitive_short")
	private short nullPrimitiveShort;
	
	@Property(name = "null_primitive_integer")
	private int nullPrimitiveInteger;
	
	@Property(name = "null_primitive_long")
	private long nullPrimitiveLong;
	
	@Property(name = "null_primitive_float")
	private float nullPrimitiveFloat;
	
	@Property(name = "null_primitive_double")
	private double nullPrimitiveDouble;
	
	@Property(name = "null_primitive_character")
	private char nullPrimitiveCharacter;
	
	@Property(name = "null_wrapper_boolean")
	private Boolean nullWrapperBoolean = null;
	
	@Property(name = "null_wrapper_byte")
	private Byte nullWrapperByte;
	
	@Property(name = "null_wrapper_short")
	private Short nullWrapperShort;
	
	@Property(name = "null_wrapper_integer")
	private Integer nullWrapperInteger;
	
	@Property(name = "null_wrapper_long")
	private Long nullWrapperLong;
	
	@Property(name = "null_wrapper_float")
	private Float nullWrapperFloat;
	
	@Property(name = "null_wrapper_double")
	private Double nullWrapperDouble;
	
	@Property(name = "null_wrapper_character")
	private Character nullWrapperCharacter;
	
	@Property(name = "null_string")
	private String nullString;
	
	@Property(name = "null_date")
	private Date nullDate;
	
	@Property(name = "null_calendar")
	private Calendar nullCalendar;
	
	@Property(name = "test_boolean")
	private Boolean testBoolean = true;
	
	@Property(name = "test_byte")
	private Byte testByte = 123;
	
	@Property(name = "test_short")
	private Short testShort = 12345;
	
	@Property(name = "test_integer")
	private Integer testInteger = 12345678;
	
	@Property(name = "test_long")
	private Long testLong = 123456789L;
	
	@Property(name = "test_float")
	private Float testFloat = 1.23456789f;
	
	@Property(name = "test_double")
	private Double testDouble = 1.23456789d;
	
	@Property(name = "test_character")
	private Character testCharacter = 'a';
	
	@Property(name = "test_string")
	private String testString = "test";
	
	@Property(name = "test_date")
	private Date testDate = new Date();
	
	@Property(name = "test_calendar")
	private Calendar testCalendar = new GregorianCalendar();
	
	@Property(name = "null_string_array")
	private String[] nullStringArray;
	
	@Property(name = "empty_string_array")
	private String[] emptyStringArray = new String[]{};
	
	@Property(name = "test_string_array")
	//private String[] testStringArray = new String[]{"one", "two", "three"};
	private String[] testStringArray;
	
	@Property(name = "null_string_list")
	private LinkedList<String> nullStringList;
	
	@Property(name = "empty_string_list")
	private LinkedList<String> emptyStringList = new LinkedList<String>();
	
	@Property(name = "test_string_list")
	private LinkedList<String> testStringList = new LinkedList<String>(Arrays.asList(new String[]{"one", "two", "three"}));
	
	@Property(name = "null_string_map")
	private LinkedHashMap<String, String> nullStringMap;
	
	@Property(name = "empty_string_map")
	private LinkedHashMap<String, String> emptyStringMap = new LinkedHashMap<String, String>();
	
	@Property(name = "test_string_map")
	private LinkedHashMap<String, String> testStringMap = new LinkedHashMap<String, String>();
	
	@Property(name = "test_model_list")
	private LinkedList<JsonSubModel> testModelList = new LinkedList<JsonSubModel>(Arrays.asList(new JsonSubModel[]{new JsonSubModel(), new JsonSubModel()}));
	
	@Property(name = "test_model_map")
	private LinkedHashMap<String, JsonSubModel> testModelMap = new LinkedHashMap<String, JsonSubModel>();
	
	public JsonModel()
	{
		testStringMap.put("one", "value one");
		testStringMap.put("two", "value two");
		testStringMap.put("three", "value three");
		testModelMap.put("one", new JsonSubModel());
		testModelMap.put("two", new JsonSubModel());
	}
	
	public boolean isNullPrimitiveBoolean()
	{
		return nullPrimitiveBoolean;
	}

	public byte getNullPrimitiveByte()
	{
		return nullPrimitiveByte;
	}

	public short getNullPrimitiveShort()
	{
		return nullPrimitiveShort;
	}

	public int getNullPrimitiveInteger()
	{
		return nullPrimitiveInteger;
	}

	public long getNullPrimitiveLong()
	{
		return nullPrimitiveLong;
	}

	public float getNullPrimitiveFloat()
	{
		return nullPrimitiveFloat;
	}

	public double getNullPrimitiveDouble()
	{
		return nullPrimitiveDouble;
	}

	public char getNullPrimitiveCharacter()
	{
		return nullPrimitiveCharacter;
	}

	public Boolean getNullWrapperBoolean()
	{
		return nullWrapperBoolean;
	}

	public Byte getNullWrapperByte()
	{
		return nullWrapperByte;
	}

	public Short getNullWrapperShort()
	{
		return nullWrapperShort;
	}

	public Integer getNullWrapperInteger()
	{
		return nullWrapperInteger;
	}

	public Long getNullWrapperLong()
	{
		return nullWrapperLong;
	}

	public Float getNullWrapperFloat()
	{
		return nullWrapperFloat;
	}

	public Double getNullWrapperDouble()
	{
		return nullWrapperDouble;
	}

	public Character getNullWrapperCharacter()
	{
		return nullWrapperCharacter;
	}

	public String getNullString()
	{
		return nullString;
	}

	public Date getNullDate()
	{
		return nullDate;
	}

	public Calendar getNullCalendar()
	{
		return nullCalendar;
	}

	public Boolean getTestBoolean()
	{
		return testBoolean;
	}

	public Byte getTestByte()
	{
		return testByte;
	}

	public Short getTestShort()
	{
		return testShort;
	}

	public Integer getTestInteger()
	{
		return testInteger;
	}

	public Long getTestLong()
	{
		return testLong;
	}

	public Float getTestFloat()
	{
		return testFloat;
	}

	public Double getTestDouble()
	{
		return testDouble;
	}

	public Character getTestCharacter()
	{
		return testCharacter;
	}

	public String getTestString()
	{
		return testString;
	}

	public Date getTestDate()
	{
		return testDate;
	}

	public Calendar getTestCalendar()
	{
		return testCalendar;
	}

	public String[] getNullStringArray()
	{
		return nullStringArray;
	}

	public String[] getEmptyStringArray()
	{
		return emptyStringArray;
	}

	public String[] getTestStringArray()
	{
		return testStringArray;
	}

	public LinkedList<String> getNullStringList()
	{
		return nullStringList;
	}

	public LinkedList<String> getEmptyStringList()
	{
		return emptyStringList;
	}

	public LinkedList<String> getTestStringList()
	{
		return testStringList;
	}

	public LinkedHashMap<String, String> getNullStringMap()
	{
		return nullStringMap;
	}

	public LinkedHashMap<String, String> getEmptyStringMap()
	{
		return emptyStringMap;
	}

	public LinkedHashMap<String, String> getTestStringMap()
	{
		return testStringMap;
	}

	public LinkedList<JsonSubModel> getTestModelList()
	{
		return testModelList;
	}

	public LinkedHashMap<String, JsonSubModel> getTestModelMap()
	{
		return testModelMap;
	}

	public void setNullPrimitiveBoolean(boolean nullPrimitiveBoolean)
	{
		this.nullPrimitiveBoolean = nullPrimitiveBoolean;
	}

	public void setNullPrimitiveByte(byte nullPrimitiveByte)
	{
		this.nullPrimitiveByte = nullPrimitiveByte;
	}

	public void setNullPrimitiveShort(short nullPrimitiveShort)
	{
		this.nullPrimitiveShort = nullPrimitiveShort;
	}

	public void setNullPrimitiveInteger(int nullPrimitiveInteger)
	{
		this.nullPrimitiveInteger = nullPrimitiveInteger;
	}

	public void setNullPrimitiveLong(long nullPrimitiveLong)
	{
		this.nullPrimitiveLong = nullPrimitiveLong;
	}

	public void setNullPrimitiveFloat(float nullPrimitiveFloat)
	{
		this.nullPrimitiveFloat = nullPrimitiveFloat;
	}

	public void setNullPrimitiveDouble(double nullPrimitiveDouble)
	{
		this.nullPrimitiveDouble = nullPrimitiveDouble;
	}

	public void setNullPrimitiveCharacter(char nullPrimitiveCharacter)
	{
		this.nullPrimitiveCharacter = nullPrimitiveCharacter;
	}

	public void setNullWrapperBoolean(Boolean nullWrapperBoolean)
	{
		this.nullWrapperBoolean = nullWrapperBoolean;
	}

	public void setNullWrapperByte(Byte nullWrapperByte)
	{
		this.nullWrapperByte = nullWrapperByte;
	}

	public void setNullWrapperShort(Short nullWrapperShort)
	{
		this.nullWrapperShort = nullWrapperShort;
	}

	public void setNullWrapperInteger(Integer nullWrapperInteger)
	{
		this.nullWrapperInteger = nullWrapperInteger;
	}

	public void setNullWrapperLong(Long nullWrapperLong)
	{
		this.nullWrapperLong = nullWrapperLong;
	}

	public void setNullWrapperFloat(Float nullWrapperFloat)
	{
		this.nullWrapperFloat = nullWrapperFloat;
	}

	public void setNullWrapperDouble(Double nullWrapperDouble)
	{
		this.nullWrapperDouble = nullWrapperDouble;
	}

	public void setNullWrapperCharacter(Character nullWrapperCharacter)
	{
		this.nullWrapperCharacter = nullWrapperCharacter;
	}

	public void setNullString(String nullString)
	{
		this.nullString = nullString;
	}

	public void setNullDate(Date nullDate)
	{
		this.nullDate = nullDate;
	}

	public void setNullCalendar(Calendar nullCalendar)
	{
		this.nullCalendar = nullCalendar;
	}

	public void setTestBoolean(Boolean testBoolean)
	{
		this.testBoolean = testBoolean;
	}

	public void setTestByte(Byte testByte)
	{
		this.testByte = testByte;
	}

	public void setTestShort(Short testShort)
	{
		this.testShort = testShort;
	}

	public void setTestInteger(Integer testInteger)
	{
		this.testInteger = testInteger;
	}

	public void setTestLong(Long testLong)
	{
		this.testLong = testLong;
	}

	public void setTestFloat(Float testFloat)
	{
		this.testFloat = testFloat;
	}

	public void setTestDouble(Double testDouble)
	{
		this.testDouble = testDouble;
	}

	public void setTestCharacter(Character testCharacter)
	{
		this.testCharacter = testCharacter;
	}

	public void setTestString(String testString)
	{
		this.testString = testString;
	}

	public void setTestDate(Date testDate)
	{
		this.testDate = testDate;
	}

	public void setTestCalendar(Calendar testCalendar)
	{
		this.testCalendar = testCalendar;
	}

	public void setNullStringArray(String[] nullStringArray)
	{
		this.nullStringArray = nullStringArray;
	}

	public void setEmptyStringArray(String[] emptyStringArray)
	{
		this.emptyStringArray = emptyStringArray;
	}

	public void setTestStringArray(String[] testStringArray)
	{
		this.testStringArray = testStringArray;
	}

	public void setNullStringList(LinkedList<String> nullStringList)
	{
		this.nullStringList = nullStringList;
	}

	public void setEmptyStringList(LinkedList<String> emptyStringList)
	{
		this.emptyStringList = emptyStringList;
	}

	public void setTestStringList(LinkedList<String> testStringList)
	{
		this.testStringList = testStringList;
	}

	public void setNullStringMap(LinkedHashMap<String, String> nullStringMap)
	{
		this.nullStringMap = nullStringMap;
	}

	public void setEmptyStringMap(LinkedHashMap<String, String> emptyStringMap)
	{
		this.emptyStringMap = emptyStringMap;
	}

	public void setTestStringMap(LinkedHashMap<String, String> testStringMap)
	{
		this.testStringMap = testStringMap;
	}

	public void setTestModelList(LinkedList<JsonSubModel> testModelList)
	{
		this.testModelList = testModelList;
	}

	public void setTestModelMap(LinkedHashMap<String, JsonSubModel> testModelMap)
	{
		this.testModelMap = testModelMap;
	}

	@Override
	public String toString()
	{
		MapperReflector mapperReflector = new JsonReflector();
		MapperConfig mapperConfig = new MapperConfig().setPrettyPrinting(true).setSerializeNulls(true).setTimeFormat("yyyy-MM-dd HH:mm:ss");
		return mapperReflector.getMapper(mapperConfig).serialize(this);
	}
	
}