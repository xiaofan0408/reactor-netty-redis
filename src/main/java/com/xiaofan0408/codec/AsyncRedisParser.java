package com.xiaofan0408.codec;


import com.xiaofan0408.model.*;
import io.netty.util.AsciiString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Character.isDigit;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author xuzefan  2020/9/4 15:33
 */
public class AsyncRedisParser {

    private final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 5;
    private final byte CR = '\r';
    private final byte LF = '\n';

    private byte[] previousBuffer;

    private boolean failed = false;

    public List<RedisObject> feed(byte[] bytes) {
        if (failed) {
            throw new IllegalStateException("Attempt to use failed parser again");
        }

        try {
            if (previousBuffer != null) {
                bytes = concat(previousBuffer, bytes);
                previousBuffer = null;
            }

            int[] index = new int[]{0};
            int lastSafePoint = 0;
            List<RedisObject> results = new ArrayList<>();

            while (index[0] < bytes.length) {
                try {
                    results.add(readObject(bytes, index));
                    lastSafePoint = index[0];
                } catch (NeedMoreInputException e) {
                    if (lastSafePoint == 0) {
                        previousBuffer = bytes;
                    } else {
                        previousBuffer = Arrays.copyOfRange(bytes, lastSafePoint, bytes.length - lastSafePoint);
                    }
                    break;
                }
            }

            return results;
        } catch (RuntimeException | Error e) {
            failed = true;
            previousBuffer = null;
            throw e;
        }
    }

    private RedisObject readObject(byte[] bytes, int[] i) {
        if (i[0] >= bytes.length) {
            throw NeedMoreInputException.INSTANCE;
        }

        switch (bytes[i[0]++]) {
            case '+': {
                return parseSimpleString(bytes, i);
            }
            case '-': return parseError(bytes, i);
            case ':': return parseInteger(bytes, i);
            case '$': return parseBulkString(bytes, i);
            case '*': return parseArray(bytes, i);
            default:
                throw new IllegalArgumentException("Encountered unknown type marker " + ((char) bytes[i[0] - 1]));
        }
    }

    private RedisArray parseArray(byte[] bytes, int[] index) {
        long size = readLong(bytes, index);
        requireCrLf(bytes, index);
        if (size < 0) {
            throw new IllegalArgumentException("Expected a non-negative array size, but was " + size);
        } else if (size > MAX_ARRAY_SIZE) {
            throw new UnsupportedOperationException("Array had size " + size +
                    ", which is greater than Java's max array size of " + MAX_ARRAY_SIZE);
        }

        List<RedisObject> elements = new ArrayList<>((int) size);
        for (; size > 0; size--) {
            elements.add(readObject(bytes, index));
        }

        return new RedisArray(elements);
    }

    private RedisInteger parseInteger(byte[] bytes, int[] index) {
        long integer = readLong(bytes, index);
        requireCrLf(bytes, index);
        return new RedisInteger(integer);
    }

    private void requireCrLf(byte[] bytes, int[] index) {
        if (index[0] > bytes.length - 2){
            throw NeedMoreInputException.INSTANCE;
        } else if(bytes[index[0]++] != CR || bytes[index[0]++] != LF) {
            throw new IllegalArgumentException("Malformed input - expected integer to end with CRLF");
        }
    }

    private long readLong(byte[] bytes, int[] index) {
        int start = index[0];
        int end = start;

        while (end < bytes.length && (isDigit(bytes[end]) || bytes[end] == '-')) {
            end++;
        }

        if (end == bytes.length) {
            throw NeedMoreInputException.INSTANCE;
        }

        index[0] = end;
        AsciiString asciiString = new AsciiString(bytes, false);
        return asciiString.parseLong(start,end,10);
    }

    private BulkString parseBulkString(byte[] bytes, int[] index) {
        long size = readLong(bytes, index);
        requireCrLf(bytes, index);
        if (size == -1) {
            return null;
        } else if (size < 0) {
            throw new IllegalArgumentException("Parse error: expected bulk string size " + size + " to be non-negative");
        } else if (size > MAX_ARRAY_SIZE) {
            throw new UnsupportedOperationException("Bulk string has size " + size +
                    ", which is greater than Java's max array size of " + MAX_ARRAY_SIZE);
        }

        if (index[0] + size >= bytes.length) {
            throw NeedMoreInputException.INSTANCE;
        }

        int start = index[0];
        int end = start + (int) size;
        index[0] = end;
        requireCrLf(bytes, index);

        return new BulkString(Arrays.copyOfRange(bytes, start, end));
    }

    private SimpleString parseSimpleString(byte[] bytes, int[] index) {
        return new SimpleString(readString(bytes, index));
    }

    private RedisError parseError(byte[] bytes, int[] index) {
        return new RedisError(readString(bytes, index));
    }

    private String readString(byte[] bytes, int[] index) {
        int start = index[0];
        int end = start;

        for (;;) {
            if (end == bytes.length) {
                throw NeedMoreInputException.INSTANCE;
            }
            byte b = bytes[end];
            if (b == CR || b == LF) {
                break;
            }
            end++;
        }
        index[0] = end;
        requireCrLf(bytes, index);

        return new String(bytes, start, end - start, US_ASCII);
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] newBuffer = new byte[a.length + b.length];

        System.arraycopy(a, 0, newBuffer, 0, a.length);
        System.arraycopy(b, 0, newBuffer, a.length, b.length);

        return newBuffer;
    }

    private static class NeedMoreInputException extends RuntimeException {

        private static final NeedMoreInputException INSTANCE = new NeedMoreInputException();

        private NeedMoreInputException() {
            super(null, null, true, false);
        }
    }
}
