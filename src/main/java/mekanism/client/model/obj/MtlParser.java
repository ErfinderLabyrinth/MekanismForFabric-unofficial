package mekanism.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class MtlParser {

    public static void load(ObjModel model, InputStream in) throws IOException {

        Material current = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] s = line.split("\\s+");

                switch (s[0]) {

                    case "newmtl":
                        current = new Material();
                        current.name = s[1];
                        model.materials.put(current.name, current);
                        break;

                    case "Kd":
                        if (current != null) {
                            current.kd[0] = Float.parseFloat(s[1]);
                            current.kd[1] = Float.parseFloat(s[2]);
                            current.kd[2] = Float.parseFloat(s[3]);
                        }
                        break;

                    case "map_Kd":
                        if (current != null) {
                            current.mapKd = line.substring("map_Kd".length()).trim();
                        }
                        break;
                }
            }
        }
    }
}