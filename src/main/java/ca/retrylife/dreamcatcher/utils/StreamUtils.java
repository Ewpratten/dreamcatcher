package ca.retrylife.dreamcatcher.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities for working with data streams
 */
public class StreamUtils {

    /**
     * Consume a stream until the end matches a string, or hits a max length
     * 
     * @param stream Stream to read
     * @param stop   String to stop at
     * @param maxLen Max length
     * @return Read string (including end)
     * @throws IOException
     */
    public static String consumeUntil(InputStream stream, String stop, int maxLen) throws IOException {

        // Allocate a buffer
        char[] buff = new char[maxLen];
        int buffIdx = 0;

        // Convert the stop to a char array
        char[] stopChars = stop.toCharArray();

        // Start consuming the stream
        consumption: {
            while (true) {

                // Get a byte
                int data = stream.read();

                // Handle invalid data
                if (data == -1) {
                    break consumption;
                }

                // Add the byte to the buffer
                buff[buffIdx] = (char) data;
                buffIdx++;

                // End of buffer?
                if (buffIdx >= maxLen) {
                    break consumption;
                }

                // If the buffer is long enough to possibly contain the stop str
                if (buffIdx + 1 >= stop.length()) {
                    // Compare the stop string to the end of the buffer
                    comparison: {
                        for (int i = 0; i < stopChars.length; i++) {
                            if (buff[buffIdx - i] != stopChars[stopChars.length - 1 - i]) {
                                break comparison;
                            }
                        }
                        break consumption;
                    }
                }
            }
        }

        return new String(buff);

    }

}