package classes;

public class NPA {
    private Integer level;
    private String nameElem;
    private String number;
    private String name;
    private String content;
    private Float sequence;

    public NPA(Integer level, String nameElem, String number, String name, String content, Float sequence) {
        this.level = level;
        this.nameElem = nameElem;
        this.number = number;
        this.name = name;
        this.content = content;
        this.sequence = sequence;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getNameElem() {
        return nameElem;
    }

    public void setNameElem(String nameElem) {
        this.nameElem = nameElem;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float getSequence() {
        return sequence;
    }

    public void setSequence(Float sequence) {
        this.sequence = sequence;
    }
}
