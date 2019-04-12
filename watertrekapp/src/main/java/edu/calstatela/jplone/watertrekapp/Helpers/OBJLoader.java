package edu.calstatela.jplone.watertrekapp.Helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OBJLoader {
    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Float> textures = new ArrayList<>();
    ArrayList<Float> normals = new ArrayList<>();

    float[] v;
    float[] vt;
    float[] n;

    float[] loc = new float[2];

    public void readOBJ(File file){
        FileInputStream in;
        BufferedReader reader = null;
        try {
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine())!= null){
                String[] parts = line.split(" ");

                switch (parts[0]){
                    case "v":
                        vertices.add(Float.valueOf(parts[1]));
                        vertices.add(Float.valueOf(parts[2]));
                        vertices.add(Float.valueOf(parts[3]));
                        break;
                    case "vt":
                        textures.add(Float.valueOf(parts[1]));
                        textures.add(Float.valueOf(parts[2]));
                        break;
                    case "vn":
                        normals.add(Float.valueOf(parts[1]));
                        normals.add(Float.valueOf(parts[2]));
                        normals.add(Float.valueOf(parts[3]));
                        break;
                    case "#":
                        loc[0] = Float.parseFloat(parts[1]);
                        loc[1] = Float.parseFloat(parts[2]);
                }
            }

            v = new float[vertices.size()];
            vt = new float[textures.size()];
            n = new float[normals.size()];
            int i = 0;
            for(float val : vertices){
                v[i] = val;
                i++;
            }

            i=0;
            for(float val: textures){
                vt[i]=val;
                i++;
            }
            i=0;
            for(float val: normals){
                n[i]= val;
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader != null){
                try {
                    reader.close();
                }catch (IOException e){

                }
            }
        }

    }

    public float[] getV() {
        return v;
    }

    public float[] getVt() {
        return vt;
    }

    public float[] getLoc() {
        return loc;
    }

    public float[] getN() {
        return n;
    }
}
