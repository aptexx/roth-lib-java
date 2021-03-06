package roth.lib.java.test.xml;

import roth.lib.java.mapper.MapperConfig;
import roth.lib.java.xml.XmlMapper;

public class XmlTest
{
	protected static MapperConfig mapperConfig = new MapperConfig().setSerializeNulls(true);
	
	public static void main(String[] args)
	{
		serializeModel();
		//deserializeModel();
	}
	
	protected static void serializeModel()
	{
		XmlTestModel model = new XmlTestModel();
		model.init();
		new XmlMapper(mapperConfig).setPrettyPrint(true).setContext("test").serialize(model, System.out);
	}
	
	protected static void deserializeModel()
	{
		String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test  day=\"2016-06-24\" attribute=\"test\" mandatory=\"true\" validate=\"false\"><test-value type=\"ERROR\">TEST</test-value><null-primitive-boolean>false</null-primitive-boolean><null-primitive-byte>0</null-primitive-byte><null-primitive-short>0</null-primitive-short><null-primitive-integer>0</null-primitive-integer><null-primitive-long>0</null-primitive-long><null-primitive-float>0.0</null-primitive-float><null-primitive-double>0.0</null-primitive-double><null-primitive-character> </null-primitive-character><null-wrapper-boolean></null-wrapper-boolean><null-wrapper-byte></null-wrapper-byte><null-wrapper-short></null-wrapper-short><null-wrapper-integer></null-wrapper-integer><null-wrapper-long></null-wrapper-long><null-wrapper-float></null-wrapper-float><null-wrapper-double></null-wrapper-double><null-wrapper-character></null-wrapper-character><null-string></null-string><null-date></null-date><null-calendar></null-calendar><test-boolean>true</test-boolean><test-byte>123</test-byte><test-short>12345</test-short><test-integer>12345678</test-integer><test-long>123456789</test-long><test-float>1.2345679</test-float><test-double>1.23456789</test-double><test-character>a</test-character><test-string>&lt;test&gt;He said, &quot;He&apos;s nice &amp; smart&quot;&lt;/test&gt;</test-string><test-date>2015-02-07 21:09:26</test-date><test-calendar>2015-02-07 21:09:26</test-calendar><test-sub><test-boolean>true</test-boolean><test-byte>123</test-byte><test-short>12345</test-short><test-integer>12345678</test-integer><test-long>123456789</test-long><test-float>1.2345679</test-float><test-double>1.23456789</test-double><test-character>a</test-character><test-string>test</test-string><test-date>2015-02-07 21:09:26</test-date><test-calendar>2015-02-07 21:09:26</test-calendar></test-sub><null-string-array></null-string-array><test-string-array><element>one</element><element>two</element><element>three</element></test-string-array><null-string-list></null-string-list><test-string-list><element>one</element><element>two</element><element>three</element></test-string-list><null-string-map></null-string-map><empty-string-map></empty-string-map><test-string-map><one>value one</one><two>value two</two><three>value three</three></test-string-map><test-model-list><element><test-boolean>true</test-boolean><test-byte>123</test-byte><test-short>12345</test-short><test-integer>12345678</test-integer><test-long>123456789</test-long><test-float>1.2345679</test-float><test-double>1.23456789</test-double><test-character>a</test-character><test-string>test</test-string><test-date>2015-02-07 21:09:26</test-date><test-calendar>2015-02-07 21:09:26</test-calendar></element><element><test-boolean>true</test-boolean><test-byte>123</test-byte><test-short>12345</test-short><test-integer>12345678</test-integer><test-long>123456789</test-long><test-float>1.2345679</test-float><test-double>1.23456789</test-double><test-character>a</test-character><test-string>test</test-string><test-date>2015-02-07 21:09:26</test-date><test-calendar>2015-02-07 21:09:26</test-calendar></element></test-model-list><test-model-map><one><test-boolean>true</test-boolean><test-byte>123</test-byte><test-short>12345</test-short><test-integer>12345678</test-integer><test-long>123456789</test-long><test-float>1.2345679</test-float><test-double>1.23456789</test-double><test-character>a</test-character><test-string>test</test-string><test-date>2015-02-07 21:09:26</test-date><test-calendar>2015-02-07 21:09:26</test-calendar></one><two><test-boolean>true</test-boolean><test-byte>123</test-byte><test-short>12345</test-short><test-integer>12345678</test-integer><test-long>123456789</test-long><test-float>1.2345679</test-float><test-double>1.23456789</test-double><test-character>a</test-character><test-string>test</test-string><test-date>2015-02-07 21:09:26</test-date><test-calendar>2015-02-07 21:09:26</test-calendar></two></test-model-map></test>";
		XmlTestModel model = new XmlMapper(mapperConfig).setPrettyPrint(true).deserialize(data, XmlTestModel.class);
		System.out.println(model);
	}
	
}
