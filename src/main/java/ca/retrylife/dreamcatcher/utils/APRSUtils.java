package ca.retrylife.dreamcatcher.utils;

import java.util.List;

import net.ab0oo.aprs.parser.APRSTypes;
import net.ab0oo.aprs.parser.Digipeater;

public class APRSUtils {

    public static String toTypeString(APRSTypes type) {
        switch (type) {
            case T_ITEM:
                return ("item");

            case T_KILL:
                return ("kill");

            case T_MESSAGE:
                return ("message");

            case T_NORMAL:
                return ("normal");

            case T_NWS:
                return ("news");

            case T_OBJECT:
                return ("object");

            case T_POSITION:
                return ("position");

            case T_QUERY:
                return ("query");

            case T_STATCAPA:
                return ("statcapa");

            case T_STATUS:
                return ("status");

            case T_TELEMETRY:
                return ("telemetry");

            case T_THIRDPARTY:
                return ("third_part");

            case T_UNSPECIFIED:
                return ("unspecified");

            case T_USERDEF:
                return ("user_defined");

            case T_WX:
                return ("weather");

            default:
                return ("unknown");

        }
    }

    public static String[] digipeatersToCallsigns(List<Digipeater> digipeaters) {
        String[] callsigns = new String[digipeaters.size()];
        for (int i = 0; i < digipeaters.size(); i++) {
            callsigns[i] = digipeaters.get(i).getCallsign();
        }
        return callsigns;
    }

}