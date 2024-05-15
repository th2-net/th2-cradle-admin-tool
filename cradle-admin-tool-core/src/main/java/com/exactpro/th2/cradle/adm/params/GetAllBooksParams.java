package com.exactpro.th2.cradle.adm.params;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class GetAllBooksParams {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllBooksParams.class);

    public enum SortType {
        NONE,
        ASC,
        DESC
    }

    private SortType  nameSort;
    private SortType creationSort;
    private final Instant from;
    private final Instant to;

    public GetAllBooksParams(String nameSort, String creationSort, Instant from, Instant to) {
        if (from != null) {
            this.from = from;
        } else {
            this.from = Instant.MIN;
        }

        if (to != null) {
            this.to = to;
        } else {
            this.to = Instant.MAX;
        }

        if (!this.from.isBefore(this.to)) {
            throw new IllegalArgumentException("Invalid date arguments, start date should be greater than end date");
        }

        try {
            if (nameSort == null) {
                this.nameSort = SortType.NONE;
            } else {
                this.nameSort = SortType.valueOf(nameSort.toUpperCase());
            }
            if (creationSort == null) {
                this.creationSort = SortType.NONE;
            } else {
                this.creationSort =SortType.valueOf(creationSort.toUpperCase());
            }

        } catch (Exception e) {
            LOGGER.warn("Exception during getting sort orders, {}", e.getMessage());
            LOGGER.info("Executing with no sorts");
            this.nameSort = GetAllBooksParams.SortType.NONE;
            this.creationSort = GetAllBooksParams.SortType.NONE;
        }
    }

    public SortType getNameSort() {
        return nameSort;
    }

    public SortType getCreationSort() {
        return creationSort;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }
}
