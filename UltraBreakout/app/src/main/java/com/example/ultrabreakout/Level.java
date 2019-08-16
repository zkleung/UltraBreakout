package com.example.ultrabreakout;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * Levels are read from 16x16 csv files, where each index contains a tile corresponding to tile
 * position.
 * 0 - Empty Tile
 * 1 - Bricks
 * 2 - Spikes
 * 3 - Balls (unused)
 */

class Level {
    public static  int NUM_ROWS = 16;
    public static  int NUM_COLUMNS = 16;
    List<List<String>> csv_file_data = new ArrayList<List<String>>();

    public Level(String csv_file_name, Context myContext){
        csv_file_data =  read_level(csv_file_name, myContext);
    }

    //read csv file
    //https://stackoverflow.com/questions/19974708/reading-csv-file-in-resources-folder-android
    private List<List<String>> read_level(String csv_file_name, Context myContext){
        String path = "levels/" + csv_file_name;

        AssetManager assetManager = myContext.getAssets();
        try{
            InputStream csvStream = assetManager.open(path);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            BufferedReader reader = new BufferedReader(csvStreamReader);
            String line;
            for(int i =0; i < NUM_ROWS; i++){
                line = reader.readLine();
                csv_file_data.add(read_level_row(line));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return csv_file_data;
    }

    //read row in csv file
    private List<String> read_level_row(String line){
        List<String> row = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)){
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                row.add(rowScanner.next());
            }
        }
        return row;
    }


}
