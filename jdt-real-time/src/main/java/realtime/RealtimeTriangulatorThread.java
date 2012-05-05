package realtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import delaunay_triangulation.Triangle_dt;

/**
 * This class is responsible to create the triangulation and/or
 * simplification of a terrain in real-time.
 * @author user
 *
 */
public class RealtimeTriangulatorThread extends Thread {
	/**
	 * Defines how many milliseconds to sleep between each check
	 * of m_isPaused object.
	 */
	private final int NUMBER_OF_MILLISECONDS_TO_SLEEP = 1000;
	
	/**
	 * The TerrainStreamer object used to retrieve the next list of
	 * values that represents the next frame to present.
	 */
	private TerrainStreamer m_ts = null;
	
	/**
	 * The path to the Terra exe file.
	 */
	private String m_terraExePath = null;
	
	/**
	 * The folder that will hold the temporary .pgm and .smf files.
	 */
	private String m_temporaryFilesFolder = null;
	
	/**
	 * An ArrayList containing the original frames.
	 * Each value is this ArrayList if a grayscale height map (.pgm file).
	 */
	private HashMap<Integer, ArrayList<Integer>> m_originalFrames = null;
	
	/**
	 * An ArrayList containing the simplified frames.
	 * Each value is this ArrayList if a grayscale height map (.pgm file).
	 */
	private HashMap<Integer, ArrayList<Integer>> m_simplifiedFrames = null;
	
	/**
	 *  An ArrayList containing the triangulated frame.
	 *  Each value in this ArrayList is a Vector of Triangle_dt objects
	 *  that represent the triangulated simplified frame.
	 */
	private HashMap<Integer, Vector<Triangle_dt>> m_triangulatedFrames = null;
	
	/**
	 * Defines if this thread should simplify the frames.
	 */
	private boolean m_shouldSimplify = true;
	
	/**
	 * Defines if this thread should triangulate
	 * the frames.
	 */
	private boolean m_shouldTriangulate = true;
	
	/**
	 * Defines if the thread is paused.
	 */
	private boolean m_isPaused = false;
	
	/**
	 * Constructor.
	 * @param ts The TerrainStreamer object used to retrieve the next list of
	 * values that represents the next frame to present.
	 * @param terraExePath The path to the Terra exe file.
	 * @param temporaryFilesFolder The path to the folder that will hold the 
	 * temporary .pgm and .smf files.
	 * @param shouldSimplify Defines if this thread should simplify the frames.
	 * @param shouldTriangulate Defines if this thread should triangulate
	 * the frames.
	 * NOTE: this path must end with "/" or "\".
	 * @throws Exception 
	 */
	public RealtimeTriangulatorThread(TerrainStreamer ts,
			String terraExePath,
			String temporaryFilesFolder,
			boolean shouldSimplify,
			boolean shouldTriangulate) throws Exception {
		if (terraExePath == null) {
			throw new Exception("Terra executable path is null!");
		}
		
		if (temporaryFilesFolder == null) {
			throw new Exception("Temporary files folder path is null!");
		}
		
		m_ts = ts;
		m_terraExePath = terraExePath;
		m_temporaryFilesFolder = temporaryFilesFolder;
		m_shouldSimplify = shouldSimplify;
		m_shouldTriangulate = shouldTriangulate;
		
		m_originalFrames = new HashMap<Integer, ArrayList<Integer>>();
		m_simplifiedFrames = new HashMap<Integer, ArrayList<Integer>>();
		m_triangulatedFrames = new HashMap<Integer, Vector<Triangle_dt>>();
	}

	@Override
	public void run() {
		try {
			Integer currentFrameIndex = 0;
			while (true) {
				while (m_isPaused == false) {
					// Get the next list of values
					ArrayList<Integer> values = m_ts.getNextListOfValues();
					
					// Add the values to the original frames list
					addOriginalFrame(values, currentFrameIndex);
					
					// Create the paths to the output files
					String pgmFileName = m_temporaryFilesFolder + 
						Consts.PGM_FILE_NAME + currentFrameIndex.toString() + Consts.PGM_FILE_EXT;
					String outputFileName = m_temporaryFilesFolder +
						Consts.TERRA_OUTPUT_FILE_NAME + currentFrameIndex.toString();
					
					// Create the .pgm file out of the .dat file values
					new DatToPgmTranslator(values, pgmFileName);
					
					if (m_shouldSimplify == true) {
						// Simplify the above .pgm file
						TerrainSimplifierThread tst = new TerrainSimplifierThread(this,
								pgmFileName, m_terraExePath, outputFileName, currentFrameIndex);
						tst.start();
					}
					
					if (m_shouldTriangulate == true) {
						TerrainTriangulatorThread ttt = new TerrainTriangulatorThread(this,
								pgmFileName, m_terraExePath, outputFileName, currentFrameIndex);
						ttt.start();
					}
					
					currentFrameIndex++;
				}
				
				while(m_isPaused == true) {
					sleep(NUMBER_OF_MILLISECONDS_TO_SLEEP);
				}
			}
		} catch(Exception ex) {
			System.out.println(this.getClass().getSimpleName() + "-> " +
					"an error has occurred: " + ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Adds an original frame values to the queue.
	 * 
	 * @param values The ArrayList containing the values of the original frame.
	 * @param index The index in which to insert the frame.
	 */
	public synchronized void addOriginalFrame(ArrayList<Integer> values, int index) {
		m_originalFrames.put(index, values);
		
		System.out.println("Added original frame");
	}
	
	/**
	 * Returns an ArrayList containing the values of the original frame.
	 * 
	 * @param index The index of the frame requested.
	 * 
	 * @return An ArrayList containing the values of the original frame.
	 * @throws InterruptedException 
	 */
	public synchronized ArrayList<Integer> getOriginalFrame(int index) throws InterruptedException {
		return m_originalFrames.get(index);
	}
	
	/**
	 * Removes an original frame.
	 *
	 * @param index The index of the original frame to remove.
	 */
	public synchronized void deleteOriginalFrame(int index) {
		m_originalFrames.remove(index);
		
		System.out.println("Deleted original frame");
	}
	
	/**
	 * Adds a simplified frame values to the queue.
	 * 
	 * @param values The ArrayList containing the values of the simplified frame.
	 * @param index The index in which to insert the frame.
	 */
	public synchronized void addSimplifiedFrame(ArrayList<Integer> values, int index) {
		m_simplifiedFrames.put(index, values);
		
		System.out.println("Added simplified frame");
	}
	
	/**
	 * Returns an ArrayList containing the values of the simplified frame.
	 * 
	 * @param index The index of the frame requested.
	 * 
	 * @return An ArrayList containing the values of the simplified frame.
	 * @throws InterruptedException 
	 */
	public synchronized ArrayList<Integer> getSimplifiedFrame(int index) throws InterruptedException {
		return m_simplifiedFrames.get(index);
	}
	
	/**
	 * Removes an simplified frame.
	 *
	 * @param index The index of the simplified frame to remove.
	 */
	public synchronized void deleteSimplifiedFrame(int index) {
		m_simplifiedFrames.remove(index);
		System.out.println("Deleted simplified frame");
	}
	
	/**
	 * Adds a triangulated frame triangles to the queue.
	 * 
	 * @param triangles The Vector containing the triangles of the triangulated frame.
	 * @param index The index in which to insert the frame.
	 */
	public synchronized void addTriangulatedFrame(Vector<Triangle_dt> triangles, int index) {
		m_triangulatedFrames.put(index, triangles);
		
		System.out.println("Added triangulated frame");
	}
	
	/**
	 * Returns an ArrayList containing the values of the triangulated frame.
	 * 
	 * @param index The index of the frame requested.
	 * 
	 * @return An ArrayList containing the values of the triangulated frame.
	 * @throws InterruptedException 
	 */
	public synchronized Vector<Triangle_dt> getTriangulatedFrame(int index) throws InterruptedException {
		return m_triangulatedFrames.get(index);
	}
	
	/**
	 * Removes an triangulated frame.
	 *
	 * @param index The index of the triangulated frame to remove.
	 */
	public synchronized void deleteTriangulatedFrame(int index) {
		m_triangulatedFrames.remove(index);
		
		System.out.println("Deleted triangulated frame");
	}
	
	/**
	 * Sets whether this thread should simplify the frames.
	 *
	 * @param shouldSimplify Whether this thread should simplify the frames.
	 */
	public void setShouldSimplify(boolean shouldSimplify) {
		m_shouldSimplify = shouldSimplify;
	}
	
	/**
	 * Sets whether this thread should triangulate the frames.
	 *
	 * @param shouldSimplify Whether this thread should triangulate the frames.
	 */
	public void setShouldTriangulate(boolean shouldTriangulate) {
		m_shouldTriangulate = shouldTriangulate;
	}
	
	/**
	 * Pauses the thread.
	 */
	public void setPaused() {
		m_isPaused = true;
	}
	
	/**
	 * Resumes the thread.
	 */
	public void setResumed() {
		m_isPaused = false;
	}
}
