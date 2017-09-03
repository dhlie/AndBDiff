package d.hl.andbdiff;

/**
 * Created by dhl on 17-9-3.
 */

public class BDiffUtil {

    static {
        System.loadLibrary("bsdiff");
        System.loadLibrary("bspatch");
    }

    /**
     * 生成差分包
     * @param oldFile       ：旧文件
     * @param newFile       ：新文件
     * @param patchFile     ：生成的差分包
     */
    public static native void applyDiff(String oldFile, String newFile, String patchFile);

    /**
     * 根据旧文件和差分包合并生成新文件
     * @param oldFile       ：旧文件
     * @param newFile       ：新文件
     * @param patchFile     ：生成的差分包
     */
    public static native void applyPatch(String oldFile, String newFile, String patchFile);
}
