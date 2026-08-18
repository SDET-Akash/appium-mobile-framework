package com.automation.mobile.exceptions;

/**
 * Thrown when an interaction with a page/screen element fails in a way
 * that should be surfaced as a framework-level failure (e.g. element
 * never became interactable, an unexpected screen state) — raised from
 * within page object methods.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class PageOperationException extends FrameworkException {

    public PageOperationException(String message) {
        super(message);
    }

    public PageOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
