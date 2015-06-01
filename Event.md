## Event Model ##

The MaJorToM engine provides an event management mechanism which enables the possibility of listening to each event or even special events of the topic map.

Applications which wants to listen to an event of the topic map has to implement the ITopicMapListener interface of the MaJorToM API.

```

public class MyListener implements ITopicMapListener{

  @Override
  public void topicMapChanged(String id, TopicMapEventType event, Construct notifier,      
                                                      Object newValue, Object oldValue) {
     ...
  }

}

```

Addionally each listener has to registered at the topic map, it wants to listen to.

```
MyListener listener = new MyListener();
topicMap.addTopicMapListener(listener );
```

If an application wants to stp listening the listener can be unregistered, too.

```
topicMap.removeTopicMapListener(listener );
```

The MaJorToM event manager notifies each event to all registered listeners. Therefore the method **topicMapChanged** will be called with the following attributes.

|**param**|**description**|
|:--------|:--------------|
|id       |the internal id of this event|
|event    |an enumeration value representing the kind of modification|
|notifier |the context of modification (e.g. the topic which get a new name)|
|newValue |the new value if a construct was created or its value was modified (can be null)|
|oldValue |the old value if a construct was removed or its value was modified (can be null)|