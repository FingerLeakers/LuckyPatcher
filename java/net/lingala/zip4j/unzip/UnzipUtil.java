package net.lingala.zip4j.unzip;

import java.io.File;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.util.InternalZipConstants;
import net.lingala.zip4j.util.Zip4jUtil;
import pxb.android.axml.ValueWrapper;
import pxb.android.axmlLP.AxmlVisitor;

public class UnzipUtil {
    public static void applyFileAttributes(FileHeader fileHeader, File file) throws ZipException {
        applyFileAttributes(fileHeader, file, null);
    }

    public static void applyFileAttributes(FileHeader fileHeader, File file, UnzipParameters unzipParameters) throws ZipException {
        if (fileHeader == null) {
            throw new ZipException("cannot set file properties: file header is null");
        } else if (file == null) {
            throw new ZipException("cannot set file properties: output file is null");
        } else if (Zip4jUtil.checkFileExists(file)) {
            if (unzipParameters == null || !unzipParameters.isIgnoreDateTimeAttributes()) {
                setFileLastModifiedTime(fileHeader, file);
            }
            if (unzipParameters == null) {
                setFileAttributes(fileHeader, file, true, true, true, true);
            } else if (unzipParameters.isIgnoreAllFileAttributes()) {
                setFileAttributes(fileHeader, file, false, false, false, false);
            } else {
                boolean z;
                boolean z2;
                boolean z3;
                boolean z4 = !unzipParameters.isIgnoreReadOnlyFileAttribute();
                if (unzipParameters.isIgnoreHiddenFileAttribute()) {
                    z = false;
                } else {
                    z = true;
                }
                if (unzipParameters.isIgnoreArchiveFileAttribute()) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                if (unzipParameters.isIgnoreSystemFileAttribute()) {
                    z3 = false;
                } else {
                    z3 = true;
                }
                setFileAttributes(fileHeader, file, z4, z, z2, z3);
            }
        } else {
            throw new ZipException("cannot set file properties: file doesnot exist");
        }
    }

    private static void setFileAttributes(FileHeader fileHeader, File file, boolean setReadOnly, boolean setHidden, boolean setArchive, boolean setSystem) throws ZipException {
        if (fileHeader == null) {
            throw new ZipException("invalid file header. cannot set file attributes");
        }
        byte[] externalAttrbs = fileHeader.getExternalFileAttr();
        if (externalAttrbs != null) {
            switch (externalAttrbs[0]) {
                case AxmlVisitor.TYPE_REFERENCE /*1*/:
                    if (setReadOnly) {
                        Zip4jUtil.setFileReadOnly(file);
                        return;
                    }
                    return;
                case ValueWrapper.STYLE /*2*/:
                case AxmlVisitor.TYPE_INT_BOOLEAN /*18*/:
                    if (setHidden) {
                        Zip4jUtil.setFileHidden(file);
                        return;
                    }
                    return;
                case AxmlVisitor.TYPE_STRING /*3*/:
                    if (setReadOnly) {
                        Zip4jUtil.setFileReadOnly(file);
                    }
                    if (setHidden) {
                        Zip4jUtil.setFileHidden(file);
                        return;
                    }
                    return;
                case InternalZipConstants.FILE_MODE_ARCHIVE /*32*/:
                case InternalZipConstants.FOLDER_MODE_ARCHIVE /*48*/:
                    if (setArchive) {
                        Zip4jUtil.setFileArchive(file);
                        return;
                    }
                    return;
                case InternalZipConstants.FILE_MODE_READ_ONLY_ARCHIVE /*33*/:
                    if (setArchive) {
                        Zip4jUtil.setFileArchive(file);
                    }
                    if (setReadOnly) {
                        Zip4jUtil.setFileReadOnly(file);
                        return;
                    }
                    return;
                case InternalZipConstants.FILE_MODE_HIDDEN_ARCHIVE /*34*/:
                case InternalZipConstants.FOLDER_MODE_HIDDEN_ARCHIVE /*50*/:
                    if (setArchive) {
                        Zip4jUtil.setFileArchive(file);
                    }
                    if (setHidden) {
                        Zip4jUtil.setFileHidden(file);
                        return;
                    }
                    return;
                case InternalZipConstants.FILE_MODE_READ_ONLY_HIDDEN_ARCHIVE /*35*/:
                    if (setArchive) {
                        Zip4jUtil.setFileArchive(file);
                    }
                    if (setReadOnly) {
                        Zip4jUtil.setFileReadOnly(file);
                    }
                    if (setHidden) {
                        Zip4jUtil.setFileHidden(file);
                        return;
                    }
                    return;
                case InternalZipConstants.FILE_MODE_SYSTEM /*38*/:
                    if (setReadOnly) {
                        Zip4jUtil.setFileReadOnly(file);
                    }
                    if (setHidden) {
                        Zip4jUtil.setFileHidden(file);
                    }
                    if (setSystem) {
                        Zip4jUtil.setFileSystemMode(file);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private static void setFileLastModifiedTime(FileHeader fileHeader, File file) throws ZipException {
        if (fileHeader.getLastModFileTime() > 0 && file.exists()) {
            file.setLastModified(Zip4jUtil.dosToJavaTme(fileHeader.getLastModFileTime()));
        }
    }
}
