//Source file: C:\\project\\ss\\engine\\CoreEngineImpl.java

package project.ss.engine;

import java.io.*;
import project.ss.exception.*;
import project.ss.xml.*;
import project.ss.engine.*;
import javax.sound.sampled.*;

/**
 * This interface provides the implementation of the interface CoreEngine. It 
 * produces the speech using a concatenation approach it achieves it by making it 
 * self the umbrella class whith instances of Player, Concatenater and 
 * IPAToPartneme.
 * The class support only set of IPA Chars specific to Gujarati.
 * The unit of concatenation used is partneme.
 * The class expects the IPA input in certain logical sequence, and otherwise 
 * throws Improper IPA Sequence exception.
 * The sequence should be.
 * {[utterance]  [utterance] [utterance]}
 * {[utterance]  [utterance] [utterance]}  
 * {[utterance]  [utterance] [utterance]}
 * 
 * 
 * Where "{" and  "}" stands for opening and closing sentence boundaries     
 * respectively. "[" and "]" denotes utterance boundaries.
 * utterance = represents any recognized IPA characters sequence without    
 * any white space characters.
 * 
 * Note:- There should be no white space characters between utterance and  
 *            utterance boundaries.
 */
public class CoreEngineImpl implements CoreEngine 
{
   static boolean shldThrowImproperDataFeed = false;
   
   /**
    * The Player used for audio playback
    */
   static Player player = null;
   
   /**
    * The Concatenater used to join different speech segments and suppy it 
    * for playback
    */
   static Concatenater conca = null;
   
   /**
    * It is used to handle the GSX Files.
    */
   static GSXHandler gsxHandler = null;
   
   /**
    * The IPAToPartnermeTranslater used in this class
    */
   static IPAToPartnemeTranslator ipaToPart = null;
   
   static 
   {
    try
    {
    File dir = new File ("./project/voice");
    File voiceFile = new File ( dir , "Combine" + ".wav" );   // "sargam"
    File voiceInfoFile = new File ( dir , "AllFilesInfo" + ".txt" );
    conca = new  ConcatenaterImpl (voiceFile ,voiceInfoFile);
    }
    catch (UnsupportedAudioFileException e )
    {
      e.printStackTrace();
    }
    catch (IOException e )
    {
      e.printStackTrace();
    }
    try
    {
    player = new ControledPlayerImpl ();
    conca.prepareSourceDataLine(player);
    }
    catch (LineUnavailableException e )
    {
     e.printStackTrace();
    }
    File dir = new File ("./project/ss/engine");
    gsxHandler =  new GSXHandlerImpl();
    File exPartnemeLookUpFile = new File (dir,"ExPartnemeLookUp.txt");
    ipaToPart = new IPAToPartnemeTranslatorImpl (exPartnemeLookUpFile,"UTF-16BE");
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @param GSXFile
    * @throws project.ss.exception.UnsupportedIPA
    * @throws project.ss.exception.ImproperIPASequence
    * @throws project.ss.exception.ImproperDataFeed
    * @throws project.ss.exception.ImproperGSXFile
    * @throws java.io.IOException
    * @roseuid 3AFF70FB0214
    */
   public void speak(File GSXFile) throws UnsupportedIPA, ImproperIPASequence, ImproperDataFeed, ImproperGSXFile, IOException 
   {   // only IPA
   try
    {
     String IPAString =  gsxHandler.getIPAString ( GSXFile );
     System.out.println("________IPA  String ________ " + IPAString);
     speak( IPAString);
    }
   catch (UnsupportedIPA e)
    {
     throw e;
    }
   catch(ImproperIPASequence e)
    {
     throw e;
    }
   catch (ImproperDataFeed e)
    {
     throw e;
    }    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * @roseuid 3AFF70FB025C
    */
   public void play() 
   {
    player.play();    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * @roseuid 3AFF70FB026F
    */
   public void stop() 
   {
    player.stop();    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @param p
    * @roseuid 3AFF70FB028D
    */
   public void setPlayer(Player p) 
   {
    player=p;    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @param dmpFile
    * @roseuid 3AFF70FB02BF
    */
   public void setDumpFile(File dmpFile) 
   {
     ((ConcatenaterImpl)conca).setDumpFile(dmpFile);
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @param con
    * @roseuid 3AFF70FB02DD
    */
   public void setConcatenater(Concatenater con) 
   {
   conca = con;    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @param voiceFile
    * @param voiceInfoFile
    * @throws javax.sound.sampled.UnsupportedAudioFileException
    * @throws java.io.IOException
    * @roseuid 3AFF70FB0305
    */
   public void setVoice(File voiceFile, File voiceInfoFile) throws UnsupportedAudioFileException, IOException 
   {
   try
    {
     conca.setVoice(voiceFile,voiceInfoFile );
    }
   catch (UnsupportedAudioFileException e )
    {
      throw e;
    }
   catch (IOException e )
    {
      throw e;
    }    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @param IPAString
    * @throws project.ss.exception.UnsupportedIPA
    * @throws project.ss.exception.ImproperIPASequence
    * @throws project.ss.exception.ImproperDataFeed
    * @roseuid 3B06A64A03C3
    */
   public void speak(String IPAString) throws UnsupportedIPA, ImproperIPASequence, ImproperDataFeed 
   {
    try
      {
       isValidIPAString(IPAString) ;
       String partnemeString = getPartnemeString(IPAString);//consists the finalString.
       concatenateAndFeed(partnemeString);
      }
    catch (UnsupportedIPA e)
      {
       throw e;
      }
    catch(ImproperIPASequence e)
      {
       throw e;
      }
    catch (ImproperDataFeed e)
      {
       throw e;
      }    
   }
   
   /**
    * Retrives the partneme string sequnce from the IPAToPartnemeConverter
    * 
    * @param validIPAString
    * @return java.lang.String
    * @throws project.ss.exception.ImproperIPASequence
    * @roseuid 3B06A64B00C2
    */
   public String getPartnemeString(String validIPAString) throws ImproperIPASequence 
   {
   try
    {
      return ipaToPart.getPartnemeString(validIPAString);
    }
   catch(ImproperIPASequence e)
    {
      throw e;
    }    
   }
   
   /**
    * Returns true if specified string it is a valid IPA string
    *  otherwise throws throws the exception
    * 
    * @param IPAString
    * @return boolean
    * @throws project.ss.exception.UnsupportedIPA
    * @roseuid 3B06A64B0130
    */
   public boolean isValidIPAString(String IPAString) throws UnsupportedIPA 
   {
   try
    {
     IPAChars.isValidIPAString(IPAString );
     return  true;     
    }
    catch (UnsupportedIPA e)
    {
 //     e.printStackTrcae();
       throw e;
    }    
   }
   
   /**
    * @return javax.sound.sampled.SourceDataLine
    * @roseuid 3B06A64B01C6
    */
   public SourceDataLine getSourceDataLine() 
   {
   return   player.getSourceDataLine ();    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @return project.ss.engine.Player
    * @roseuid 3B06A64B0220
    */
   public Player getPlayer() 
   {
   return player;    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @return java.io.File
    * @roseuid 3B06A64B0248
    */
   public File getDumpFile() 
   {
    return player.getDumpFile();    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * 
    * @return project.ss.engine.Concatenater
    * @roseuid 3B06A64B02DF
    */
   public Concatenater getConcatenater() 
   {
   return conca ;    
   }
   
   /**
    * Provides the implementation of the same method described in CoreEngine
    * @param partnemeString
    * @throws project.ss.exception.ImproperDataFeed -  included for testing
    * @roseuid 3B06A64C0019
    */
   public void concatenateAndFeed(String partnemeString) throws ImproperDataFeed 
   {
   // new thread
   shldThrowImproperDataFeed=false;
   final String partnemeStr =  partnemeString;
   SwingWorker worker = new SwingWorker()
     {

         public Object construct() 
         {
           //...code that might take a while to execute is here...
           try
            {
             conca.concatenateAndFeed(partnemeStr);
            }
            catch (ImproperDataFeed e)
            {
              shldThrowImproperDataFeed=true;
            }
            finally
            {
             return "999";
            } 
         }
     };
    worker.start();  //required for SwingWorker 3
    if (shldThrowImproperDataFeed)
    {
     throw new  ImproperDataFeed (" Data was not feeded thoroughly");
    }    
   }
}
