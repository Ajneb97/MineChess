package mc.ajneb97.versions;

import java.lang.reflect.Method;
import java.util.HashMap;

public class VersionItem {
    private HashMap<String,Class<?>> classes;
    private HashMap<String, Method> methods;

    public VersionItem(){
        this.classes = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public void addClass(String name,Class<?> classType){
        classes.put(name,classType);
    }

    public void addMethod(String name,Method methodType){
        methods.put(name,methodType);
    }

    public Class<?> getClassRef(String name){
        return classes.get(name);
    }

    public Method getMethodRef(String name){
        return methods.get(name);
    }
}
