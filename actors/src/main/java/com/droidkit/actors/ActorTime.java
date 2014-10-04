package com.droidkit.actors;

/**
 * Time used by actor system, uses System.nanoTime() inside
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class ActorTime {
    /**
     * Getting current actor system time
     *
     * @return actor system time
     */
    public static long currentTime() {
        return System.nanoTime() / 1000000;
    }
}