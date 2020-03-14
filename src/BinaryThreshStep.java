import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConfigLength;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayU8;

class BinaryThreshStep extends ProcessStep
{
    @Override
    protected Image process(BufferedImage in) {
        GrayU8 img = ConvertBufferedImage.convertFromSingle(in, null, GrayU8.class);
        GrayU8 binary = img.createSameShape();
        GThresholdImageOps.localMean(img, binary, ConfigLength.fixed(57), 1.0, true, null, null,null);

        GrayU8 filtered = BinaryImageOps.erode8(binary, 5, null);

        GrayS32 label = new GrayS32(img.width,img.height);
        int colorExternal = 0xFFFFFF;
		int colorInternal = 0xFF2020;

        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, label);
        BufferedImage vis = VisualizeBinaryData.renderContours(contours, colorExternal, colorInternal,img.width, img.height, null);
        return vis;
    }

    @Override
	protected boolean handleClick(java.awt.event.MouseEvent e) {
		return false;
	}
}