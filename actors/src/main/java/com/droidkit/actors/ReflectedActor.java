package com.droidkit.actors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by ex3ndr on 19.08.14.
 */
public class ReflectedActor extends Actor {

    private ArrayList<Event> events = new ArrayList<Event>();

    @Override
    public final void preStart() {
        Method[] methods = getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("onReceive") && m.getParameterTypes().length == 1) {
                if (m.getName().equals("onReceive") && m.getParameterTypes()[0] == Object.class) {
                    continue;
                }
                events.add(new Event(m.getParameterTypes()[0], m));
            }
        }
        preStartImpl();
    }

    public void preStartImpl() {

    }

    @Override
    public void onReceive(Object message) {
        for (Event event : events) {
            if (event.check(message)) {
                try {
                    event.method.invoke(this, message);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    class Event {
        private Class arg;
        private Method method;

        Event(Class arg, Method method) {
            this.arg = arg;
            this.method = method;
        }

        public boolean check(Object obj) {
            if (arg.isAssignableFrom(obj.getClass())) {
                return true;
            }
            return false;
        }
    }
}
