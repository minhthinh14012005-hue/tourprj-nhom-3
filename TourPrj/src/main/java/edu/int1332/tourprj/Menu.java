package edu.int1332.tourprj;

import java.util.ArrayList;

/** Console input boundary. It owns numeric recovery and generic list selection. */
public final class Menu {
    private Menu() { }

    public static <E> int intGetChoice(ArrayList<E> options) { return -1; }
    public static <E> E refGetChoice(ArrayList<E> options) { return null; }
}
