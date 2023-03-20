package com.crihexe;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import kotlin.text.Charsets;

public class HTMLtoJS {
	
	private static final String SRC_PATH = "C:\\Users\\Cristian\\Documents\\temp\\html.html";
	private static final String DST_PATH = "C:\\Users\\Cristian\\Documents\\temp\\js.js";
	
	private String element;
	private int unnamedCounter = 0;
	
	public HTMLtoJS(String element) {
		this.element = element;
	}
	
	public static void main(String[] args) throws Exception {
		String HTML = Files.readString(new File(SRC_PATH).toPath(), Charsets.UTF_8);
		
		HTMLtoJS parser = new HTMLtoJS(HTML);
		
		String JS = parser.parse();
		
		System.out.println(JS);
		Files.writeString(new File(DST_PATH).toPath(), JS, StandardOpenOption.CREATE);
	}
	
	public String parse() {
		return addChild("// PARENT_HERE", Jsoup.parse(element, Parser.xmlParser()).child(0));
	}
	
	public String addChild(String parentName, Element child) {
		String js = "";
		
		String tag;
		String name;
		String className;
		
		if(!child.classNames().iterator().hasNext()) {
			tag = child.tag().normalName();
			name = tag + unnamedCounter++;
			className = "";
		} else {
			tag = child.tag().normalName();
			name = child.classNames().iterator().next().replaceAll("-", "_");
			className = child.className();
		}

		js += "let " + name + " = document.createElement(\"" + tag + "\");\n";
		if(!className.isBlank()) js += name + ".className = \"" + className + "\";\n";
		if(child.hasText()) if(!child.wholeOwnText().trim().isBlank()) js += name + ".textContent = \"" + child.wholeOwnText().trim() + "\";\n";
		js += addAttributes(name, child);
		
		js += "\n";
		
		for(int i = 0; i < child.childrenSize(); i++)
			js += addChild(name, child.children().get(i)) + "\n";
		
		js += parentName + ".appendChild(" + name + ");\n";
		
		return js;
	}
	
	public String addAttributes(String name, Element e) {
		List<Attribute> attributes = e.attributes().asList();
		String js = "";
		
		for(int i = 0; i < attributes.size(); i++) if(!attributes.get(i).getKey().equals("class")) js += name + ".setAttribute(\"" + attributes.get(i).getKey() + "\", \"" + attributes.get(i).getValue() + "\");\n";
		
		return js;
	}
	
}
