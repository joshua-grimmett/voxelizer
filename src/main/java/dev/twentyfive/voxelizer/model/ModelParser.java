package dev.twentyfive.voxelizer.model;

import dev.twentyfive.voxelizer.math.Triangle;
import dev.twentyfive.voxelizer.math.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelParser {

    private static String getPathToFile(String filename) {
        return Paths.get("").toAbsolutePath()
                + FileSystems.getDefault().getSeparator()
                + "plugins"
                + FileSystems.getDefault().getSeparator()
                + "Voxelizer"
                + FileSystems.getDefault().getSeparator()
                + filename;
    }

    public static Model parseModelFromFile(String filename, double scale) {
        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<Vector3> uvs = new ArrayList<>();
        ArrayList<Triangle> triangles = new ArrayList<>();

        Map<String, ModelMaterial> materials = new HashMap<>();
        String materialsFolderPath = new File(getPathToFile(filename)).getParent();

        try (BufferedReader br = new BufferedReader(new FileReader(getPathToFile(filename)))) {
            ModelMaterial currentMaterial = null;

            for (String line; (line = br.readLine()) != null; ) {
                if (line.startsWith("v ")) {
                    String[] xyz = line.substring(2).trim().split(" ");
                    Vector3 vertex = new Vector3(Double.parseDouble(xyz[0]), Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2]));
                    vertices.add(Vector3.multiply(vertex, scale));
                    continue;
                }

                if (line.startsWith("vt ")) {
                    String[] uv = line.substring(3).trim().split(" ");
                    uvs.add(new Vector3(Double.parseDouble(uv[0]), Double.parseDouble(uv[1]), 0));
                    continue;
                }

                if (line.startsWith("f ")) {
                    String[] abc = line.substring(2).trim().split(" ");
                    String[] aSplit = abc[0].split("/");
                    String[] bSplit = abc[1].split("/");
                    String[] cSplit = abc[2].split("/");

                    int a = Integer.parseInt(aSplit[0]) - 1;
                    int b = Integer.parseInt(bSplit[0]) - 1;
                    int c = Integer.parseInt(cSplit[0]) - 1;
                    int aUV = (aSplit.length > 1 ? Integer.parseInt(aSplit[1]) : 0) - 1;
                    int bUV = (bSplit.length > 1 ? Integer.parseInt(bSplit[1]) : 0) - 1;
                    int cUV = (cSplit.length > 1 ? Integer.parseInt(cSplit[1]) : 0) - 1;

                    triangles.add(new Triangle(a, b, c, aUV, bUV, cUV, currentMaterial));
                    continue;
                }

                if (line.startsWith("usemtl ")) {
                    String materialName = line.substring(7).trim();
                    currentMaterial = materials.getOrDefault(materialName, null);
                    continue;
                }

                if (line.startsWith("mtllib ")) {
                    String materialLibFilename = line.substring(6).trim();
                    materials.putAll(parseMaterialLib(materialsFolderPath, materialLibFilename));
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Model(vertices.toArray(Vector3[]::new), triangles.toArray(Triangle[]::new), uvs.toArray(Vector3[]::new));
    }

    private static Map<String, ModelMaterial> parseMaterialLib(String folderPath, String filename) {
        Map<String, ModelMaterial> materials = new HashMap<>();

        String materialLibPath = folderPath + FileSystems.getDefault().getSeparator() + filename;

        try (BufferedReader br = new BufferedReader(new FileReader(materialLibPath))) {
            String currentMaterialName = "";

            for (String line; (line = br.readLine()) != null; ) {
                if (line.startsWith("newmtl ")) {
                    currentMaterialName = line.substring(7).trim();
                    continue;
                }

                if (line.startsWith("map_") && currentMaterialName.length() > 0) {
                    String[] split = line.split(" ");
                    String textureFilename = split[split.length - 1];
                    String texturePath = folderPath + FileSystems.getDefault().getSeparator() + textureFilename;
                    BufferedImage texture = ImageIO.read(new File(texturePath));
                    materials.put(currentMaterialName, new ModelMaterial(texture));
                    currentMaterialName = "";
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return materials;
    }

}
