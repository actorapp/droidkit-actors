DroidKit Actors
===============
Lightweight java implementation of actor model for small applications. Designed for Android applications.
Read more about actors on [Wikipedia](http://en.wikipedia.org/wiki/Actor_model)

QuickStart
===============
### Add dependency to your gradle project
```
compile 'com.droidkit:actors:0.1.+'
```

### Create log Actor
```
import android.util.Log;
import com.droidkit.actors.Actor;

public class LogActor extends Actor {

    @Override
    public void preStart() {
        Log.d("ACTOR", "preStart #" + hashCode());
    }

    @Override
    public void onReceive(Object message) {
        Log.d("ACTOR", message + "");
    }

    @Override
    public void postStop() {
        Log.d("ACTOR", "postStop #" + hashCode());
    }
}
```

### Use main actor system
Actor system is entry point to actor model, it contains all configurations, dispatchers and actors.
Dispatcher is a queue + worker threads for this queue.

By default ActorSystem has static main ActorSystem and in most cases you can use it in two ways:
```
void a() {
  ActorSystem.system()
}
```
or
```
import static com.droidkit.actors.ActorSystem.system;

void a() {
  system()
}
```
### or create your Actor system
```
ActorSystem system = new ActorSystem();
// Add additional dispatcher with threads number == cores count
system.addDispatcher("images");
// Add additional dispather with 3 threads with minimal priority
system.addDispatcher("images", new MailboxesDispatcher(system, 2, Thread.MIN_PRIORITY));
```
### Creating and using actor
```
ActorRef log1 = system().actorOf(LogActor.class, "log");
ActorRef log2 = system().actorOf(LogActor.class, "log");
ActorRef log3 = system().actorOf(LogActor.class, "log2");
// log4 will be executed on dispatcher "images"
ActorRef log4 = system().actorOf(Props.create(LogActor.class).changeDispatcher("images"), "log3");

assert log1 == log2
assert log3 != log2

log1.send("test1");
log2.send("test2");
log3.send("test3");
log4.send("test4");
```

ActorSytem
===============

License
===============
License use [MIT License](LICENSE)
