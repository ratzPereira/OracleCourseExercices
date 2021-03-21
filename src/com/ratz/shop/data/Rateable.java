package com.ratz.shop.data;


@FunctionalInterface
public interface Rateable<T> {


    public static final Rating DEFAULT_RATING = Rating.NOT_RATED;


    T applyRating(Rating rating);

    default Rating getRating() {

        return DEFAULT_RATING;
    }

    static Rating convert(int stars) {

        //enum returns .values() from the position of the enumeric values. only enum has the method values()
        return (stars >= 0 && stars <= 5) ? Rating.values()[stars] : DEFAULT_RATING;
    }

    default T applyRating (int stars){

        return applyRating(convert(stars));
    }
}
