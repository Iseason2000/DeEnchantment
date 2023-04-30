package top.iseason.bukkittemplate.runtime;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML文件依赖解析器
 */
public class XmlParser {
    public static final Pattern placeHolder = Pattern.compile("\\$\\{(.*)}");

    Document doc;

    public XmlParser(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(file);
    }

    /**
     * 解析XML文件依赖,返回格式化的依赖
     */
    public List<String> getDependencies() {
        List<String> list = new ArrayList<>();
        NodeList dependencies = doc.getElementsByTagName("dependencies");
        for (int n = 0; n < dependencies.getLength(); n++) {
            Node item = dependencies.item(n);
            if (item == null) return list;
            NodeList childNodes = item.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element dependency = (Element) node;
                NodeList groupIdNode = dependency.getElementsByTagName("groupId");
                if (groupIdNode.getLength() == 0) continue;
                String groupId = groupIdNode.item(0).getTextContent();
                NodeList artifactIdNode = dependency.getElementsByTagName("artifactId");
                if (artifactIdNode.getLength() == 0) continue;
                String artifactId = artifactIdNode.item(0).getTextContent();
                NodeList versionNode = dependency.getElementsByTagName("version");
                if (versionNode.getLength() == 0) continue;
                String version = versionNode.item(0).getTextContent();
                NodeList optionalNode = dependency.getElementsByTagName("optional");
                if (optionalNode.getLength() != 0 && optionalNode.item(0).getTextContent().equalsIgnoreCase("true"))
                    continue;
                NodeList scopeNode = dependency.getElementsByTagName("scope");
                if (scopeNode.getLength() != 0) {
                    String scope = scopeNode.item(0).getTextContent();
                    if (!(scope.equalsIgnoreCase("compile") || scope.equalsIgnoreCase("runtime"))) {
                        continue;
                    }
                }
                // 无法处理 区间依赖
                if (version.contains("[") || version.contains("(")) {
                    list.add(groupId + ":" + artifactId + ":" + version);
                } else {
                    Matcher matcher = placeHolder.matcher(version);
                    if (matcher.find()) {
                        String group = matcher.group(1);
                        NodeList elementsByTagName = doc.getElementsByTagName(group);
                        if (elementsByTagName.getLength() > 0)
                            version = elementsByTagName.item(0).getTextContent();
                    }
                    list.add(groupId + ":" + artifactId + ":" + version);
                }
            }
        }
        return list;
    }

}
