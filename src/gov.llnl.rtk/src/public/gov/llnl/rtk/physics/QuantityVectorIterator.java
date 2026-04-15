// --- file: gov/llnl/rtk/physics/QuantityVectorIterator.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Efficient iterator over a vector of primitive values, presenting each as a {@link Quantity} with the specified units.
 * <p>
 * <b>Performance Note:</b> This iterator reuses a single mutable {@code Quantity} instance for all elements.
 * <b>Do not retain references</b> to the returned {@code Quantity} outside the current iteration.
 * If you need to store a value, make a defensive copy (e.g., {@code Quantity.of(q.getValue(), q.getUnits())}).
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 * QuantityVectorIterator it = new QuantityVectorIterator(data, Units.get("keV"));
 * while (it.hasNext()) {
 *     Quantity q = it.next();
 *     // Use q immediately; do not store q for later use!
 * }
 * </pre>
 * </p>
 *
 * @author (your name or team)
 */
public class QuantityVectorIterator implements Iterator<Quantity>
{
    private final double[] values;
    private final Units units;
    private int index = 0;
    private final QuantityImpl mutableQuantity;

    /**
     * Constructs an iterator over a vector of primitive values, presenting each as a {@link Quantity} with the specified units.
     * @param values the array of primitive values (not null)
     * @param units the units to associate with each value (not null)
     */
    public QuantityVectorIterator(double[] values, Units units)
    {
        if (values == null) throw new NullPointerException("values array is null");
        if (units == null) throw new NullPointerException("units is null");
        this.values = values;
        this.units = units;
        this.mutableQuantity = new QuantityImpl(0, units, 0, true);
    }

    @Override
    public boolean hasNext()
    {
        return index < values.length;
    }

    /**
     * Returns the next {@link Quantity} in the vector.
     * <b>This instance is reused for all elements!</b>
     * <p>
     * <b>Warning:</b> The returned {@code Quantity} is only valid until the next call to {@code next()}.
     * If you need to retain a value, make a defensive copy.
     * </p>
     * @return the next quantity in the iteration
     * @throws NoSuchElementException if no more elements
     */
    @Override
    public Quantity next()
    {
        if (!hasNext()) throw new NoSuchElementException();
        mutableQuantity.assign(values[index++], units);
        return mutableQuantity;
    }

    /**
     * Not supported. This iterator is read-only.
     * @throws UnsupportedOperationException always
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("remove not supported");
    }

    /**
     * Resets the iterator to the start of the vector.
     */
    public void reset()
    {
        this.index = 0;
    }

    /**
     * Returns the number of elements in the vector.
     * @return the vector length
     */
    public int size()
    {
        return values.length;
    }
}