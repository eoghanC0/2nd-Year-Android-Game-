package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.os.Environment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.DemoGame;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by stephenmcveigh on 24/01/2018.
 */

@RunWith(MockitoJUnitRunner.class)
public class FileIOTest {
    private Context appContext;
    private FileIO fileIO;

    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();

    @Before
    public void setup() {
        when(Environment.getExternalStorageDirectory()).thenReturn(storageDirectory.getRoot());
        appContext = mock(Context.class);
        fileIO = new FileIO(appContext);
    }

    @Test
    public void test_isExternalStorageWritable_isWritable() {
        when(Environment.getExternalStorageState()).thenReturn(Environment.MEDIA_MOUNTED);
        assert(fileIO.isExternalStorageWritable());
    }

    @Test
    public void test_isExternalStorageWritable_isNotWritable() {
        when(Environment.getExternalStorageState()).thenReturn(Environment.MEDIA_REMOVED);
        assert(!fileIO.isExternalStorageWritable());
    }

    @Test
    public void test_isExternalStorageReadable_isReadable() {
        //The disk is always readable when it is writable
        when(Environment.getExternalStorageState()).thenReturn(Environment.MEDIA_MOUNTED);
        assert(fileIO.isExternalStorageReadable());
    }

    @Test
    public void test_isExternalStorageReadable_isReadOnly() {
        when(Environment.getExternalStorageState()).thenReturn(Environment.MEDIA_MOUNTED_READ_ONLY);
        assert(fileIO.isExternalStorageReadable());
    }

    @Test
    public void test_isExternalStorageReadable_dismounted() {
        when(Environment.getExternalStorageState()).thenReturn(Environment.MEDIA_REMOVED);
        assert(!fileIO.isExternalStorageReadable());
    }

    @Test
    public void test_readFile_diskNotReadable() throws IOException {
        when(Environment.getExternalStorageState()).thenReturn(Environment.MEDIA_REMOVED);
        assertEquals("", fileIO.readFile("mockFileName.txt"));
    }

    @Test
    public void test_readFile_fileDoesNotExist() throws IOException {
        when(fileIO.isExternalStorageReadable()).thenReturn(true);
        assertEquals("",fileIO.readFile("mockFileName.txt"));
    }
//
//    @Test
//    public void test_readFile_fileExistsAndIsReadable() throws IOException{
//
//        storageDirectory.newFile("testFile.txt");
//
//        //write it
//        BufferedWriter bw = new BufferedWriter(new FileWriter(storageDirectory.getRoot() + File.separator + "testFile.txt"));
//        bw.write("This is the temporary file content");
//        bw.close();
//
//        when(fileIO.isExternalStorageReadable()).thenReturn(true);
//
//    }

}
