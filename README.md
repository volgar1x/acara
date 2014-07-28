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
  compile 'com.github.blackrush.acara:acara-core:1.3'
}
```

Examples
========

```java
class SomeEvent {
  final String someValue;
}

class SomeListener {
  @Listener
  public void someEventListener(SomeEvent evt) {
    // use SomeEvent as you want
    // here, I just log its value
    System.out.println(evt.someValue);
  }
}

Worker worker = Workers.wrap(Executors.newSingleThreadExecutor());
EventBus eventBus = CoreEventBus.create(worker);

SomeListener listener = new SomeListener();
eventBus.subscribe(listener);
try {
  eventBus.publish(new SomeEvent("hello, world!"));
} finally {
  eventBus.unsubscribe(listener);
}
```
