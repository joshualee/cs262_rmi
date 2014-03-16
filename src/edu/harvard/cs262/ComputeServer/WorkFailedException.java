package edu.harvard.cs262.ComputeServer;

/**
 * WorkFailedException is an exception that is thrown when work
 * is sent to a remote server, which then fails to respond to ping.
 */
public class WorkFailedException extends Exception {
  private static final long serialVersionUID = 1L;
}
