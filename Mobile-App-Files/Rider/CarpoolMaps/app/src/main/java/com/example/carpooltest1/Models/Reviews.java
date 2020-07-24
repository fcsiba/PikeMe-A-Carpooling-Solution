package com.example.carpooltest1.Models;

public class Reviews {String Review;
    int Rating;
    String From;

    public Reviews(String review, int rating, String from) {
        Review = review;
        Rating = rating;
        From = from;
    }

    public String getReview() {
        return Review;
    }

    public int getRating() {
        return Rating;
    }

    public String getFrom() {
        return From;
    }
}
