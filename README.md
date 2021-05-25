# Laniax's Event Dispatcher


A java event dispatching framework based on the mediator pattern. Influenced by Symfony's Event Dispatcher. Usable by any Java program, originally written for use inside TRiBot scripts.


### What is this?

This little framework allows you to write events, raise them and listen to them. Sounds cool, right..?
Well it is way more useful than it sounds. The very purpose of this system is to be decouple any logic from a framework, but stay insanely flexible. Or in simpler terms, able to do completely different things without changing any existing code.

### Okay i still don't get it, show me an example!

You bet! There are only 3 components in this entire library, the ``Dispatcher``, ``Event`` and ``EventListener``.
And what's a better way to explain it than in code, here a very basic example:

```java
public void run() {
    
    Dispatcher.getInstance()
        .addListener(Event.class, new EventListener<Event>((event) -> System.out.println("Event was raised!"));
    
    ... 
    
    while(doWork) {
        
        if (somethingHappened) {
            Dispatcher.getInstance().dispatch(new Event());
        }
        
    }
}
```


##### Now you might think, what's the point? Can't i just write it there directly?
Well sure nobody is stopping you, but that doesn't mean you _should_. In this example it's just one line of code, and within the same class, with a basic event, but things get a lot better when you expand upon it..

### Usage
This framework was originally written for TRiBot scripts, because it suits it very well, so below you will find an example of such.
```java
public class ExampleScript extends Script {
    
    public void run() {
    
        Dispatcher.getInstance()
            .addListener(WalkEvent.class, new DaxWalkerListener(), 10)
            .addListener(WalkEvent.class, new TribotWalkerListener(), 5)
            .addListener(WalkEvent.class, new BlindWalkerListener(), 1);
        
        while(doLoop) {
            
            if (shouldBank()) {
            
                if (isAtBank())
                    doBank();
                else 
                    walkToBank();
            }
            
            if (shouldCutTree()) {
            
                if (isAtTree())
                    cutTree();
                else 
                    walkToTree();
            }
        }
    }
    
    private boolean walkToBank() {
        // When we want to walk to the bank, we simply dispatch an event with the tile we want to walk to
        // Now any subscribers can take care of walking there for us.
        WalkEvent event = Dispatcher.getInstance().dispatch(new WalkEvent(VARS.BANK_POSITION));
        
        // At this point in code, all the listeners have already run, 
        // and we can tell if the walk was successful or not.
        return event.isWalkSuccessful();
    }
    
    private boolean walkToTrees() {
        // this is the same event as above, but with a different tile
        WalkEvent event = Dispatcher.getInstance().dispatch(new WalkEvent(VARS.TREE_POSITION));

        ...
    }
    
    ...
}
```

Now when walking to the bank or trees, an event will be raised, and the 3 listeners will be invoked (in order). The first one that get's a crack at it will be DaxWalkerListener, since it's priority is the highest (10).


```java
public class DaxWalkerListener extends EventListener<WalkEvent> {
    
    @Override
    protected Consumer<WalkEvent> onEvent() {
        return (event) -> {
            
            RSTile position = event.getPosition();
            
            if (DaxWalker.walkTo(position)) {
            
                // If daxwalker was successful, we don't need the other walkers to go at it.
                // So we say to the dispatcher that this listener should be the last
                event.stopPropagation();
                
                // We also want to let our original code know that the walk completed successfully,
                // So we can pass back any data we want, in this case, we simply set a boolean to true.
                event.setWalkSuccessful(true);
            }
            
            // If propagation wasn't stopped, the next listener will be called, in this case, TribotWalkerListener, etc.
        }
    }
```

The event class would look like this.

```java
public class WalkEvent extends Event {
    
    private RSTile position;
    private boolean walkSuccessful = false;
    
    public WalkEvent(RSTile position) {
        this.position = position;
    }
    
    public RSTile getPosition() {
        return this.position;
    }
    
    public boolean isWalkSuccessful() {
        return this.walkSuccessful;
    }
    
    public void setWalkSuccessful(boolean value) {
        this.walkSuccessful = value;
    }
}
```

#### Thoughts
Now even that was a rather simple example, it can be so much more. You can use it for say, logging to different outputs.
But you could also trigger an event when the Bank was opened and have a listener withdraw all the items, something like that would be especially useful if you have an api that handles running to the bank and opening it. It allows you to completely decouple your logic from the process, but still have a ton of flexibility by simply adding a new listener to the same event.

I also love to use it with MessageListening, PreBreaking and even my paint! (Simply dispatch a 'PaintEvent' in your onPaint() method, and suddenly you can paint in every part of your code!)

There are a ton of possibilities, and i hope you can see the power of the event dispatcher :)


Oh! and it's also fully unit tested.

License
----

Apache 2


