package Game.Network;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author SALEHSAGHARCHI
 * Date: 2018-07-21
 * Time: 3:33 AM
 */
public class Temp {
    private ObjectOutputStream objectOutputStream;

    public Temp(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public void println(String toSend) {
        try {
            objectOutputStream.flush();
            objectOutputStream.writeUnshared(toSend);
            objectOutputStream.reset();
            objectOutputStream.flush();
        } catch (IOException e) {
            //e.printStackTrace();
            waitToAvailable(objectOutputStream);
        }
    }

    public static void waitToAvailable(ObjectOutputStream stream) {
        boolean isok = false;
        while (!isok) {
            try {
                stream.reset();
                isok = true;
            } catch (IOException e) {
                isok = false;
            }
        }
    }
}
