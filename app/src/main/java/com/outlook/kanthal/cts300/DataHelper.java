package com.outlook.kanthal.cts300;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CTS300";

    private static final String SQL_CREATE_TABLE_TONES =
            "CREATE TABLE Tones (" +
                    " PatchNumber INTEGER PRIMARY KEY," +
                    " PatchName TEXT," +
                    " ProgramChange INTEGER," +
                    " BankSelect INTEGER " +
                    ")";

    private static final String SQL_CREATE_TABLE_QUICK_ACCESS =
            "CREATE TABLE QuickAccessTones (" +
                    " KeyNumber INTEGER PRIMARY KEY," +
                    " PatchNumber INTEGER," +
                    " Color TEXT " +
                    ")";

    private static final String SQL_CREATE_TABLE_SETTINGS =
            "CREATE TABLE Settings (" +
                    " Name TEXT PRIMARY KEY," +
                    " Value TEXT" +
                    ")";

    private static ArrayList<String> tonesList = new ArrayList<String>();

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SETTINGS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TONES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUICK_ACCESS);
        sqLiteDatabase.execSQL(SQL_INSERT_TONES);
        sqLiteDatabase.execSQL(SQL_INSERT_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Do nothing
        if(1==2){}
    }

    public boolean saveSetting(String name, String value){
        ContentValues values = new ContentValues();
        values.put("Value", value);
        long rowId = this.getWritableDatabase().update("Settings", values, "Name = ?", new String[]{name});
        return (rowId>-1);
    }

    public String getSetting(String name){
        String query = "SELECT Value FROM Settings WHERE Name = ?";
        Cursor cursor = this.getWritableDatabase().rawQuery(query, new String[]{name});

        if(cursor.isAfterLast()){
            return null;
        }

        String value = "";
        if (cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndexOrThrow("Value"));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return value;
    }

    public boolean deleteQuickAccessTone(int keyNumber){
        long rowId = this.getWritableDatabase().delete("QuickAccessTones", "KeyNumber = ?", new String[]{keyNumber+""});
        return (rowId>-1);
    }

    public boolean saveQuickAccessTone(QuickAccessTone quickAccessTone){
        this.getWritableDatabase().delete("QuickAccessTones", "KeyNumber = ?", new String[]{quickAccessTone.keyNumber+""});

        ContentValues values = new ContentValues();
        values.put("KeyNumber", quickAccessTone.keyNumber);
        values.put("PatchNumber", quickAccessTone.patchNumber);
        values.put("Color", quickAccessTone.color);
        long rowId = this.getWritableDatabase().insert("QuickAccessTones", null, values);
        return (rowId>-1);
    }

    public Tone getTone(int patchNumber) {
        String query = "SELECT PatchNumber, PatchName, ProgramChange, BankSelect FROM Tones WHERE PatchNumber = ?";
        Cursor cursor = this.getWritableDatabase().rawQuery(query, new String[]{ (patchNumber + "") });

        if(cursor.isAfterLast()){
            return null;
        }

        ArrayList<Tone> tones = new ArrayList<Tone>();
        if (cursor.moveToFirst()) {
            do{
                Tone tone = new Tone();

                tone.patchNumber   = cursor.getInt(cursor.getColumnIndexOrThrow("PatchNumber"));
                tone.patchName     = cursor.getString(cursor.getColumnIndexOrThrow("PatchName"));
                tone.programChange = cursor.getInt(cursor.getColumnIndexOrThrow("ProgramChange"));
                tone.bankSelect    = cursor.getInt(cursor.getColumnIndexOrThrow("BankSelect"));
                tones.add(tone);
            }while(cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return tones.get(0);
    }

    public ArrayList<String> getTonesList() {
        if(tonesList.size() > 0){
            return tonesList;
        }

        String query = "SELECT PatchNumber, PatchName FROM Tones";
        Cursor cursor = this.getWritableDatabase().rawQuery(query, null);

        if(cursor.isAfterLast()){
            return null;
        }

        ArrayList<String> tonesList = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do{
                String tone = "[" + cursor.getInt(cursor.getColumnIndexOrThrow("PatchNumber")) + "] " + cursor.getString(cursor.getColumnIndexOrThrow("PatchName"));
                tonesList.add(tone);
            }while(cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return tonesList;
    }

    public ArrayList<QuickAccessTone> getQuickAccessTones(){
        String query = "SELECT a.KeyNumber, a.Color, a.PatchNumber, b.PatchName, b.ProgramChange, b.BankSelect " +
                "FROM QuickAccessTones AS a JOIN Tones AS b ON a.PatchNumber = b.PatchNumber ";

        Cursor cursor = this.getWritableDatabase().rawQuery(query, null);

        if(cursor.isAfterLast()){
            return null;
        }

        ArrayList<QuickAccessTone> tones = new ArrayList<QuickAccessTone>();
        if (cursor.moveToFirst()) {
            do{
                QuickAccessTone tone = new QuickAccessTone();

                tone.keyNumber     = cursor.getInt(cursor.getColumnIndexOrThrow("KeyNumber"));
                tone.color         = cursor.getString(cursor.getColumnIndexOrThrow("Color"));
                tone.patchNumber   = cursor.getInt(cursor.getColumnIndexOrThrow("PatchNumber"));
                tone.patchName     = cursor.getString(cursor.getColumnIndexOrThrow("PatchName"));
                tone.programChange = cursor.getInt(cursor.getColumnIndexOrThrow("ProgramChange"));
                tone.bankSelect    = cursor.getInt(cursor.getColumnIndexOrThrow("BankSelect"));
                tones.add(tone);
            }while(cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return tones;
    }

    private static final String SQL_INSERT_SETTINGS = "INSERT INTO Settings (Name, Value) VALUES (\"QUICK_ACCESS_PAGES\", 1)";

    private static final String SQL_INSERT_TONES =
            "INSERT INTO Tones (PatchNumber, PatchName, ProgramChange, BankSelect) VALUES " +
            "(1,\"STEREO GRAND PIANO\",0,2)," +
            "(2,\"GRAND PIANO\",0,1)," +
            "(3,\"BRIGHT PIANO\",1,2)," +
            "(4,\"MODERN PIANO\",1,3)," +
            "(5,\"DANCE PIANO\",1,1)," +
            "(6,\"MELLOW PIANO\",0,3)," +
            "(7,\"STRINGS PIANO\",0,8)," +
            "(8,\"HONKY-TONK\",3,2)," +
            "(9,\"OCTAVE PIANO\",3,9)," +
            "(10,\"BASS/PIANO\",0,9)," +
            "(11,\"ELEC.GRAND PIANO\",2,2)," +
            "(12,\"MODERN E.G.PIANO\",2,3)," +
            "(13,\"HARPSICHORD\",6,2)," +
            "(14,\"HARPSICHORD & STRINGS\",6,1)," +
            "(15,\"ELEC.PIANO\",4,2)," +
            "(16,\"FM E.PIANO\",5,5)," +
            "(17,\"60'S E.PIANO\",4,5)," +
            "(18,\"CHORUS E.PIANO 1\",4,9)," +
            "(19,\"CHORUS E.PIANO 2\",4,6)," +
            "(20,\"MODERN E.PIANO\",5,2)," +
            "(21,\"SOFT E.PIANO\",4,8)," +
            "(22,\"SYNTH-STR.E.PIANO\",4,3)," +
            "(23,\"CLEAN E.PIANO\",4,4)," +
            "(24,\"CLAVI 1\",7,2)," +
            "(25,\"CLAVI 2\",7,3)," +
            "(26,\"SOFT CLAVI\",7,1)," +
            "(27,\"DETUNE CLAVI\",7,8)," +
            "(28,\"SEQUENCE CLAVI\",7,9)," +
            "(29,\"VIBRAPHONE 1\",11,2)," +
            "(30,\"VIBRAPHONE 2\",11,1)," +
            "(31,\"SOFT VIBRAPHONE 1\",11,3)," +
            "(32,\"SOFT VIBRAPHONE 2\",11,4)," +
            "(33,\"MARIMBA\",12,2)," +
            "(34,\"SOFT MARIMBA\",12,1)," +
            "(35,\"CELESTA 1\",8,2)," +
            "(36,\"CELESTA 2\",8,1)," +
            "(37,\"GLOCKENSPIEL\",9,2)," +
            "(38,\"MUSIC BOX 1\",10,2)," +
            "(39,\"MUSIC BOX 2\",10,1)," +
            "(40,\"XYLOPHONE\",13,2)," +
            "(41,\"TUBULAR BELL\",14,2)," +
            "(42,\"DULCIMER\",15,2)," +
            "(43,\"DRAWBAR ORGAN 1\",16,2)," +
            "(44,\"DRAWBAR ORGAN 2\",16,1)," +
            "(45,\"PERC.ORGAN 1\",17,2)," +
            "(46,\"PERC.ORGAN 2\",17,3)," +
            "(47,\"ELEC.ORGAN 1\",16,8)," +
            "(48,\"ELEC.ORGAN 2\",16,4)," +
            "(49,\"JAZZ ORGAN 1\",17,4)," +
            "(50,\"JAZZ ORGAN 2\",17,6)," +
            "(51,\"ROCK ORGAN 1\",18,2)," +
            "(52,\"ROCK ORGAN 2\",18,1)," +
            "(53,\"FULL DRAWBAR\",16,9)," +
            "(54,\"CLICK ORGAN\",18,7)," +
            "(55,\"8'ORGAN\",17,5)," +
            "(56,\"CHURCH ORGAN 1\",19,2)," +
            "(57,\"CHURCH ORGAN 2\",19,3)," +
            "(58,\"CHAPEL ORGAN\",19,8)," +
            "(59,\"THEATER ORGAN\",19,6)," +
            "(60,\"REED ORGAN\",20,2)," +
            "(61,\"ACCORDION\",21,2)," +
            "(62,\"BANDONEON\",23,2)," +
            "(63,\"HARMONICA 1\",22,2)," +
            "(64,\"HARMONICA 2\",22,28)," +
            "(65,\"NYLON STR.GUITAR\",24,2)," +
            "(66,\"STEEL STR.GUITAR\",25,2)," +
            "(67,\"12 STR.GUITAR\",25,8)," +
            "(68,\"CHORUS STEEL GT\",25,9)," +
            "(69,\"JAZZ GUITAR\",26,2)," +
            "(70,\"OCT.JAZZ GUITAR\",26,8)," +
            "(71,\"CLEAN GUITAR 1\",27,2)," +
            "(72,\"CLEAN GUITAR 2\",27,1)," +
            "(73,\"MUTE GUITAR\",28,2)," +
            "(74,\"OVERDRIVE GUITAR\",29,2)," +
            "(75,\"DISTORTION GUITAR\",30,2)," +
            "(76,\"POWER DIST.GUITAR\",30,5)," +
            "(77,\"FEEDBACK GUITAR\",31,8)," +
            "(78,\"DIST.GUITAR & BASS\",30,6)," +
            "(79,\"ACOUSTIC BASS\",32,2)," +
            "(80,\"FINGERED BASS\",33,2)," +
            "(81,\"PICKED BASS\",34,2)," +
            "(82,\"FRETLESS BASS\",35,2)," +
            "(83,\"SLAP BASS\",37,2)," +
            "(84,\"SAW SYNTH-BASS\",38,2)," +
            "(85,\"SQUARE SYNTH-BASS\",39,2)," +
            "(86,\"DIGI ROCK BASS\",39,1)," +
            "(87,\"TRANCE BASS\",38,4)," +
            "(88,\"SINE BASS\",39,6)," +
            "(89,\"VIOLIN\",40,2)," +
            "(90,\"SLOW VIOLIN\",40,8)," +
            "(91,\"VIOLA\",41,2)," +
            "(92,\"CELLO\",42,2)," +
            "(93,\"SLOW CELLO\",42,1)," +
            "(94,\"CONTRABASS\",43,2)," +
            "(95,\"VIOLIN & CELLO\",40,3)," +
            "(96,\"CELLO SECTION\",42,4)," +
            "(97,\"PIZZICATO STRINGS\",45,2)," +
            "(98,\"HARP 1\",46,2)," +
            "(99,\"HARP 2\",46,1)," +
            "(100,\"CHORUS HARP\",46,8)," +
            "(101,\"STRINGS\",48,2)," +
            "(102,\"SLOW STRINGS\",49,2)," +
            "(103,\"WIDE STRINGS\",48,16)," +
            "(104,\"CHAMBER\",48,3)," +
            "(105,\"OCTAVE STRINGS\",48,32)," +
            "(106,\"STRINGS SFZ\",48,8)," +
            "(107,\"TREMOLO STRINGS\",49,1)," +
            "(108,\"FLUTE & STRINGS\",49,3)," +
            "(109,\"CHOIR STRINGS\",52,3)," +
            "(110,\"SYNTH-STRINGS 1\",50,2)," +
            "(111,\"SYNTH-STRINGS 2\",51,2)," +
            "(112,\"SYNTH-STRINGS 3\",51,3)," +
            "(113,\"FAST SYNTH-STRINGS\",50,3)," +
            "(114,\"CHOIR AAHS\",52,2)," +
            "(115,\"VOICE DOO\",53,2)," +
            "(116,\"SYNTH-VOICE\",54,2)," +
            "(117,\"SYNTH-VOICE PAD\",54,8)," +
            "(118,\"CHORUS SYNTH-VOICE\",54,9)," +
            "(119,\"ORCHESTRA HIT 1\",55,2)," +
            "(120,\"ORCHESTRA HIT 2\",55,1)," +
            "(121,\"TRUMPET\",56,2)," +
            "(122,\"MELLOW TRUMPET\",56,8)," +
            "(123,\"TRUMPET SFZ\",56,1)," +
            "(124,\"TROMBONE\",57,2)," +
            "(125,\"TUBA\",58,2)," +
            "(126,\"MUTE TRUMPET\",59,2)," +
            "(127,\"FRENCH HORN\",60,2)," +
            "(128,\"FRENCH HORN SECTION\",60,1)," +
            "(129,\"BRASS\",61,2)," +
            "(130,\"BRASS SECTION 1\",61,3)," +
            "(131,\"BRASS SECTION 2\",61,6)," +
            "(132,\"BRASS SECTION 3\",61,7)," +
            "(133,\"MELLOW BRASS\",61,1)," +
            "(134,\"HARD BRASS\",61,5)," +
            "(135,\"BRASS SFZ\",61,8)," +
            "(136,\"BRASS & STRINGS\",61,4)," +
            "(137,\"SYNTH-BRASS 1\",62,2)," +
            "(138,\"SYNTH-BRASS 2\",63,2)," +
            "(139,\"ANALOG SYNTH-BRASS 1\",62,8)," +
            "(140,\"ANALOG SYNTH-BRASS 2\",62,9)," +
            "(141,\"ALTO SAX 1\",65,1)," +
            "(142,\"ALTO SAX 2\",65,2)," +
            "(143,\"HARD A.SAX\",65,3)," +
            "(144,\"BREATHY A.SAX\",65,8)," +
            "(145,\"TENOR SAX\",66,1)," +
            "(146,\"SOPRANO SAX 1\",64,2)," +
            "(147,\"SOPRANO SAX 2\",64,1)," +
            "(148,\"BARITONE SAX 1\",67,2)," +
            "(149,\"BARITONE SAX 2\",67,1)," +
            "(150,\"SAX SECTION\",65,9)," +
            "(151,\"CLARINET\",71,2)," +
            "(152,\"OBOE\",68,2)," +
            "(153,\"SOLO OBOE\",68,4)," +
            "(154,\"BASSOON\",70,5)," +
            "(155,\"FLUTE 1\",73,2)," +
            "(156,\"FLUTE 2\",73,1)," +
            "(157,\"PURE FLUTE\",73,8)," +
            "(158,\"PICCOLO\",72,2)," +
            "(159,\"RECORDER\",74,2)," +
            "(160,\"PAN FLUTE\",75,2)," +
            "(161,\"BOTTLE BLOW 1\",76,2)," +
            "(162,\"BOTTLE BLOW 2\",76,1)," +
            "(163,\"WHISTLE\",78,2)," +
            "(164,\"OCARINA\",79,2)," +
            "(165,\"FLUTE & OBOE\",73,3)," +
            "(166,\"SQUARE LEAD 1\",80,2)," +
            "(167,\"SQUARE LEAD 2\",80,3)," +
            "(168,\"SQUARE LEAD 3\",80,1)," +
            "(169,\"SAW LEAD 1\",81,2)," +
            "(170,\"SAW LEAD 2\",81,1)," +
            "(171,\"SAW LEAD 3\",81,5)," +
            "(172,\"MELLOW SAW LEAD\",81,8)," +
            "(173,\"SQUARE PULSE LEAD\",80,5)," +
            "(174,\"SEQUENCE SAW\",81,9)," +
            "(175,\"SEQUENCE SINE\",80,9)," +
            "(176,\"SINE LEAD\",80,8)," +
            "(177,\"SS LEAD\",81,3)," +
            "(178,\"SEQUENCE SQUARE\",80,7)," +
            "(179,\"SEQUENCE PULSE\",80,16)," +
            "(180,\"SLOW SAW LEAD\",81,4)," +
            "(181,\"CALLIOPE\",82,2)," +
            "(182,\"VENT LEAD\",82,5)," +
            "(183,\"VENT SYNTH\",82,1)," +
            "(184,\"CHIFF LEAD\",83,2)," +
            "(185,\"SEQUENCE LEAD 1\",83,5)," +
            "(186,\"SEQUENCE LEAD 2\",83,3)," +
            "(187,\"VOICE LEAD\",85,2)," +
            "(188,\"DISTORTION LEAD\",84,8)," +
            "(189,\"CHARANG\",84,2)," +
            "(190,\"CHURCH LEAD\",85,4)," +
            "(191,\"SYNTH-VOICE LEAD\",85,7)," +
            "(192,\"FIFTH LEAD\",86,4)," +
            "(193,\"FIFTH SAW LEAD\",86,2)," +
            "(194,\"FIFTH SQUARE LEAD\",86,3)," +
            "(195,\"FIFTH SEQUENCE\",86,1)," +
            "(196,\"BASS+LEAD\",87,2)," +
            "(197,\"DANCE SQUARE LEAD\",80,48)," +
            "(198,\"DANCE SYNC SQUARE LEAD\",80,49)," +
            "(199,\"DANCE SAW LEAD\",81,48)," +
            "(200,\"DANCE SYNC SAW LEAD\",81,49)," +
            "(201,\"DANCE POLY SAW LEAD\",81,50)," +
            "(202,\"DANCE SAW BASS\",87,48)," +
            "(203,\"FANTASY 1\",88,2)," +
            "(204,\"FANTASY 2\",88,3)," +
            "(205,\"WARM VOX\",89,8)," +
            "(206,\"WARM PAD\",89,2)," +
            "(207,\"SINE PAD\",89,3)," +
            "(208,\"SOFT PAD\",89,4)," +
            "(209,\"OLD TAPE PAD\",89,6)," +
            "(210,\"POLYSYNTH 1\",90,2)," +
            "(211,\"POLYSYNTH 2\",90,1)," +
            "(212,\"POLY SAW\",90,8)," +
            "(213,\"SPACE CHOIR\",91,1)," +
            "(214,\"HEAVEN\",91,2)," +
            "(215,\"SQUARE PAD\",92,1)," +
            "(216,\"BOWED PAD\",92,2)," +
            "(217,\"GLASS PAD\",92,3)," +
            "(218,\"ETHNIC PAD\",93,2)," +
            "(219,\"HARD METAL PAD\",93,4)," +
            "(220,\"CHORUS PAD\",94,1)," +
            "(221,\"HALO PAD\",94,2)," +
            "(222,\"SWEEP PAD\",95,2)," +
            "(223,\"RAIN DROP\",96,2)," +
            "(224,\"SPACE VOICE\",97,1)," +
            "(225,\"SOUND TRACK 1\",97,2)," +
            "(226,\"SOUND TRACK 2\",97,3)," +
            "(227,\"RAVE\",97,8)," +
            "(228,\"CRYSTAL\",98,2)," +
            "(229,\"CHORAL BELL\",98,16)," +
            "(230,\"CELESTA PAD\",99,1)," +
            "(231,\"ATMOSPHERE\",99,2)," +
            "(232,\"BRIGHT BELL PAD\",100,1)," +
            "(233,\"BRIGHTNESS\",100,2)," +
            "(234,\"GOBLIN\",101,2)," +
            "(235,\"ECHO PAD\",102,2)," +
            "(236,\"ECHO DROP\",102,3)," +
            "(237,\"POLY DROP\",102,4)," +
            "(238,\"STAR THEME\",103,2)," +
            "(239,\"SPACE PAD\",103,8)," +
            "(240,\"DANCE SAW PAD\",90,48)," +
            "(241,\"DANCE SQUARE PAD 1\",90,49)," +
            "(242,\"DANCE SQUARE PAD 2\",90,50)," +
            "(243,\"SITAR 1\",104,2)," +
            "(244,\"SITAR 2\",104,3)," +
            "(245,\"TANPURA 1\",104,32)," +
            "(246,\"TANPURA 2\",104,33)," +
            "(247,\"HARMONIUM 1\",20,32)," +
            "(248,\"HARMONIUM 2\",20,33)," +
            "(249,\"SHANAI 1\",111,2)," +
            "(250,\"SHANAI 2\",111,3)," +
            "(251,\"SANTUR 1\",15,3)," +
            "(252,\"SANTUR 2\",15,4)," +
            "(253,\"TABLA\",116,16)," +
            "(254,\"YANG QIN 1\",15,8)," +
            "(255,\"YANG QIN 2\",15,9)," +
            "(256,\"DI ZI\",72,16)," +
            "(257,\"ZHENG\",107,1)," +
            "(258,\"SHENG\",109,8)," +
            "(259,\"SUO NA\",111,32)," +
            "(260,\"XIAO\",77,32)," +
            "(261,\"PI PA\",105,32)," +
            "(262,\"BANJO\",105,2)," +
            "(263,\"THUMB PIANO\",108,2)," +
            "(264,\"STEEL DRUMS\",114,2)," +
            "(265,\"RABAB\",105,8)," +
            "(266,\"KOTO\",107,2)," +
            "(267,\"GM PIANO 1\",0,0)," +
            "(268,\"GM PIANO 2\",1,0)," +
            "(269,\"GM PIANO 3\",2,0)," +
            "(270,\"GM HONKY-TONK\",3,0)," +
            "(271,\"GM E.PIANO 1\",4,0)," +
            "(272,\"GM E.PIANO 2\",5,0)," +
            "(273,\"GM HARPSICHORD\",6,0)," +
            "(274,\"GM CLAVI\",7,0)," +
            "(275,\"GM CELESTA\",8,0)," +
            "(276,\"GM GLOCKENSPIEL\",9,0)," +
            "(277,\"GM MUSIC BOX\",10,0)," +
            "(278,\"GM VIBRAPHONE\",11,0)," +
            "(279,\"GM MARIMBA\",12,0)," +
            "(280,\"GM XYLOPHONE\",13,0)," +
            "(281,\"GM TUBULAR BELL\",14,0)," +
            "(282,\"GM DULCIMER\",15,0)," +
            "(283,\"GM ORGAN 1\",16,0)," +
            "(284,\"GM ORGAN 2\",17,0)," +
            "(285,\"GM ORGAN 3\",18,0)," +
            "(286,\"GM PIPE ORGAN\",19,0)," +
            "(287,\"GM REED ORGAN\",20,0)," +
            "(288,\"GM ACCORDION\",21,0)," +
            "(289,\"GM HARMONICA\",22,0)," +
            "(290,\"GM BANDONEON\",23,0)," +
            "(291,\"GM NYLON STR.GUITAR\",24,0)," +
            "(292,\"GM STEEL STR.GUITAR\",25,0)," +
            "(293,\"GM JAZZ GUITAR\",26,0)," +
            "(294,\"GM CLEAN GUITAR\",27,0)," +
            "(295,\"GM MUTE GUITAR\",28,0)," +
            "(296,\"GM OVERDRIVE GUITAR\",29,0)," +
            "(297,\"GM DISTORTION GUITAR\",30,0)," +
            "(298,\"GM GUITAR HARMONICS\",31,0)," +
            "(299,\"GM ACOUSTIC BASS\",32,0)," +
            "(300,\"GM FINGERED BASS\",33,0)," +
            "(301,\"GM PICKED BASS\",34,0)," +
            "(302,\"GM FRETLESS BASS\",35,0)," +
            "(303,\"GM SLAP BASS 1\",36,0)," +
            "(304,\"GM SLAP BASS 2\",37,0)," +
            "(305,\"GM SYNTH-BASS 1\",38,0)," +
            "(306,\"GM SYNTH-BASS 2\",39,0)," +
            "(307,\"GM VIOLIN\",40,0)," +
            "(308,\"GM VIOLA\",41,0)," +
            "(309,\"GM CELLO\",42,0)," +
            "(310,\"GM CONTRABASS\",43,0)," +
            "(311,\"GM TREMOLO STRINGS\",44,0)," +
            "(312,\"GM PIZZICATO\",45,0)," +
            "(313,\"GM HARP\",46,0)," +
            "(314,\"GM TIMPANI\",47,0)," +
            "(315,\"GM STRINGS 1\",48,0)," +
            "(316,\"GM STRINGS 2\",49,0)," +
            "(317,\"GM SYNTH-STRINGS 1\",50,0)," +
            "(318,\"GM SYNTH-STRINGS 2\",51,0)," +
            "(319,\"GM CHOIR AAHS\",52,0)," +
            "(320,\"GM VOICE DOO\",53,0)," +
            "(321,\"GM SYNTH-VOICE\",54,0)," +
            "(322,\"GM ORCHESTRA HIT\",55,0)," +
            "(323,\"GM TRUMPET\",56,0)," +
            "(324,\"GM TROMBONE\",57,0)," +
            "(325,\"GM TUBA\",58,0)," +
            "(326,\"GM MUTE TRUMPET\",59,0)," +
            "(327,\"GM FRENCH HORN\",60,0)," +
            "(328,\"GM BRASS\",61,0)," +
            "(329,\"GM SYNTH-BRASS 1\",62,0)," +
            "(330,\"GM SYNTH-BRASS 2\",63,0)," +
            "(331,\"GM SOPRANO SAX\",64,0)," +
            "(332,\"GM ALTO SAX\",65,0)," +
            "(333,\"GM TENOR SAX\",66,0)," +
            "(334,\"GM BARITONE SAX\",67,0)," +
            "(335,\"GM OBOE\",68,0)," +
            "(336,\"GM ENGLISH HORN\",69,0)," +
            "(337,\"GM BASSOON\",70,0)," +
            "(338,\"GM CLARINET\",71,0)," +
            "(339,\"GM PICCOLO\",72,0)," +
            "(340,\"GM FLUTE\",73,0)," +
            "(341,\"GM RECORDER\",74,0)," +
            "(342,\"GM PAN FLUTE\",75,0)," +
            "(343,\"GM BOTTLE BLOW\",76,0)," +
            "(344,\"GM SHAKUHACHI\",77,0)," +
            "(345,\"GM WHISTLE\",78,0)," +
            "(346,\"GM OCARINA\",79,0)," +
            "(347,\"GM SQUARE LEAD\",80,0)," +
            "(348,\"GM SAW LEAD\",81,0)," +
            "(349,\"GM CALLIOPE\",82,0)," +
            "(350,\"GM CHIFF LEAD\",83,0)," +
            "(351,\"GM CHARANG\",84,0)," +
            "(352,\"GM VOICE LEAD\",85,0)," +
            "(353,\"GM FIFTH LEAD\",86,0)," +
            "(354,\"GM BASS+LEAD\",87,0)," +
            "(355,\"GM FANTASY\",88,0)," +
            "(356,\"GM WARM PAD\",89,0)," +
            "(357,\"GM POLYSYNTH\",90,0)," +
            "(358,\"GM SPACE CHOIR\",91,0)," +
            "(359,\"GM BOWED GLASS\",92,0)," +
            "(360,\"GM METAL PAD\",93,0)," +
            "(361,\"GM HALO PAD\",94,0)," +
            "(362,\"GM SWEEP PAD\",95,0)," +
            "(363,\"GM RAIN DROP\",96,0)," +
            "(364,\"GM SOUND TRACK\",97,0)," +
            "(365,\"GM CRYSTAL\",98,0)," +
            "(366,\"GM ATMOSPHERE\",99,0)," +
            "(367,\"GM BRIGHTNESS\",100,0)," +
            "(368,\"GM GOBLINS\",101,0)," +
            "(369,\"GM ECHOES\",102,0)," +
            "(370,\"GM SF\",103,0)," +
            "(371,\"GM SITAR\",104,0)," +
            "(372,\"GM BANJO\",106,0)," +
            "(373,\"GM SHAMISEN\",106,0)," +
            "(374,\"GM KOTO\",107,0)," +
            "(375,\"GM THUMB PIANO\",108,0)," +
            "(376,\"GM BAGPIPE\",109,0)," +
            "(377,\"GM FIDDLE\",110,0)," +
            "(378,\"GM SHANAI\",111,0)," +
            "(379,\"GM TINKLE BELL\",112,0)," +
            "(380,\"GM AGOGO\",113,0)," +
            "(381,\"GM STEEL DRUMS\",114,0)," +
            "(382,\"GM WOOD BLOCK\",116,0)," +
            "(383,\"GM TAIKO\",116,0)," +
            "(384,\"GM MELODIC TOM\",117,0)," +
            "(385,\"GM SYNTH-DRUM\",118,0)," +
            "(386,\"GM REVERSE CYMBAL\",119,0)," +
            "(387,\"GM GUITAR FRET NOISE\",120,0)," +
            "(388,\"GM BREATH NOISE\",121,0)," +
            "(389,\"GM SEASHORE\",122,0)," +
            "(390,\"GM BIRD\",123,0)," +
            "(391,\"GM TELEPHONE\",124,0)," +
            "(392,\"GM HELICOPTER\",126,0)," +
            "(393,\"GM APPLAUSE\",126,0)," +
            "(394,\"GM GUNSHOT\",127,0)," +
            "(395,\"STANDARD SET 1\",0,120)," +
            "(396,\"STANDARD SET 2\",1,120)," +
            "(397,\"DANCE SET\",29,120)," +
            "(398,\"BRUSH SET\",40,120)," +
            "(399,\"ORCHESTRA SET\",48,120)," +
            "(400,\"INDIAN SET\",49,120)";
}
