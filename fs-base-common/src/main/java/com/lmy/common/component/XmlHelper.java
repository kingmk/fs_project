package com.lmy.common.component;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.sun.tools.apt.Main;

public final class XmlHelper {

	private XmlHelper() {
	}

	public static Map<String, String> convertPrototype(String messagePrototype)
			throws DocumentException, SAXException {
		List<Element> fields = getFields(messagePrototype, "map");

		Map<String, String> paramMap = new LinkedHashMap<String, String>();
		for (Element field : fields) {
			String fieldName = field.attributeValue("name");
			String fieldValue = field.getText();
			paramMap.put(fieldName, fieldValue);
		}

		return paramMap;
	}

	public static Map<String, String> getParams(Element paramsElement) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		List<Element> paramElements = children(paramsElement, "param");
		for (Element param : paramElements) {

			String text = param.getText();
			params.put(param.attributeValue("name"), text);
		}

		return params;
	}

	public static List<Element> getFields(String xml, String node)
			throws DocumentException, SAXException {
		Element root = getField(xml);
		return children(root, node);
	}

	public static List<Element> getFields(String xml, String node,
			String subNode) throws DocumentException, SAXException {
		Element root = getField(xml);
		Element nodeElement = child(root, node);
		return children(nodeElement, subNode);
	}

	public static Element getField(String xml) throws DocumentException, SAXException {
		StringReader stringReader = null;

		try {
			stringReader = new StringReader(xml);
			SAXReader reader = new SAXReader();
			String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
			reader.setFeature(FEATURE, true);
	        FEATURE = "http://xml.org/sax/features/external-general-entities";
	        reader.setFeature(FEATURE, false);
	        FEATURE = "http://xml.org/sax/features/external-parameter-entities";
	        reader.setFeature(FEATURE, false);
	        FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
	        reader.setFeature(FEATURE, false);

			Document doc = reader.read(stringReader);
			return doc.getRootElement();
		} finally {
			IOUtils.closeQuietly(stringReader);
		}

	}

	/**
	 * Return the child element with the given name. The element must be in the
	 * same name space as the parent element.
	 * 
	 * @param element
	 *            The parent element
	 * @param name
	 *            The child element name
	 * @return The child element
	 */
	public static Element child(Element element, String name) {
		return element.element(new QName(name, element.getNamespace()));
	}

	/**
	 * Return the descendant element with the given xPath. Remember to remove
	 * the heading and tailing backslash ( / )
	 * 
	 * @param element
	 *            The parent element
	 * @param xPath
	 *            e.g: "foo/bar"
	 * @return The child element. Return null if any sub node name is not
	 *         matched
	 */
	public static Element descendant(Element element, String xPath) {
		if (element == null || xPath == null || xPath.trim().isEmpty()) {
			return null;
		}

		String[] paths = xPath.split("/");

		Element tempElement = element;
		for (String nodeName : paths) {
			tempElement = child(tempElement, nodeName);
		}
		return tempElement;
	}

	/**
	 * Return the child elements with the given name. The elements must be in
	 * the same name space as the parent element.
	 * 
	 * @param element
	 *            The parent element
	 * @param name
	 *            The child element name
	 * @return The child elements
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> children(Element element, String name) {
		return element.elements(new QName(name, element.getNamespace()));
	}

	/**
	 * Return the value of the child element with the given name. The element
	 * must be in the same name space as the parent element.
	 * 
	 * @param element
	 *            The parent element
	 * @param name
	 *            The child element name
	 * @return The child element value
	 */
	public static String elementAsString(Element element, String name) {
		return element.elementText(new QName(name, element.getNamespace()));
	}

	/**
	 */
	public static int elementAsInteger(Element element, String name) {
		String text = elementAsString(element, name);
		if (text == null) {
			return 0;
		}

		return Integer.parseInt(text);
	}

	
	public static boolean elementAsBoolean(Element element, String name) {
		String text = elementAsString(element, name);
		if (text == null) {
			return false;
		}
		return Boolean.valueOf(text);
	}

	public static void main(String[] args) {
		String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE root [<!ENTITY xxe SYSTEM \"http://192.168.31.113:8080/order/notify_url\">]><foo><value>&xxe;</value></foo>";
//		String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><foo><value>abc</value></foo>";
		
		
		try {
			XmlHelper.getField(xmlString);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}
