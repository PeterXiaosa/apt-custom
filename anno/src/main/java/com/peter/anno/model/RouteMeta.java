package com.peter.anno.model;

import com.peter.anno.BindView;

import javax.lang.model.element.Element;

public class RouteMeta {
    private BindView bindView;
    private Element element;
    private String value;
    private Class<?> cls;

    public RouteMeta(BindView bindView, Element element) {
        this.bindView = bindView;
        this.element = element;
    }

    public RouteMeta(BindView bindView, Element element, String value, Class<?> cls) {
        this.bindView = bindView;
        this.element = element;
        this.value = value;
        this.cls = cls;
    }

    public static RouteMeta build(String value, Class<?> cls) {
        return new RouteMeta(null, null, value, cls);
    }

    public BindView getBindView() {
        return bindView;
    }

    public void setBindView(BindView bindView) {
        this.bindView = bindView;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }
}
