acara [![Build Status](https://travis-ci.org/Blackrush/acara.svg)](https://travis-ci.org/Blackrush/acara)
=====

acara, "event" in Javanese

Quick start
===========

### Gradle

```groovy

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  compile 'com.github.blackrush.acara:acara-core-java:2.0-alpha1'
}
```

Examples
========

```java
class SomeEvent {
    final String someValue;
}

class SomeListener {
    @Listen
    public void someEventListener(SomeEvent evt) {
        // use SomeEvent as you want
        // here, I just log its value
        System.out.println(evt.someValue);
    }
}

EventBus eventBus = Acara.newEventBus(
    new JavaEventMetadataBuilder(),
    new JavaListenerBuilder(),
    Workers.wrap(Executors.newSingleThreadExecutor()));

Subscription sub = eventBus.subscribe(listener);
try {
    eventBus.publish(new SomeEvent("hello, world!"));
} finally {
    sub.revoke();
}
```
