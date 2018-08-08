package classes;

public class Element {
    private String num;
    private String name;
    private String content;
    private String elementType;

    public Element(String num, String name, String content, String elementType) {
        this.num = num;
        this.name = name;
        this.content = content;
        this.elementType = elementType;
    }

    public String getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getElementType() {
        return elementType;
    }
}
