import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConfigLength;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayU8;

class ContourBoxStep extends ProcessStep
{
    ArrayList<ContourBoundingBox> bounds = new ArrayList<ContourBoundingBox>();
    @Override
    protected Image process(BufferedImage in) {
        GrayU8 img = ConvertBufferedImage.convertFromSingle(in, null, GrayU8.class);
        GrayU8 binary = img.createSameShape();
        GThresholdImageOps.localMean(img, binary, ConfigLength.fixed(20), 1.0, true, null, null,null);

        GrayU8 filtered = BinaryImageOps.erode8(binary, 2, null);
        GrayS32 label = new GrayS32(img.width,img.height);

        double imgArea = img.height*img.width;

        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, label);
        bounds.clear();
        for(Contour contour:contours)
        {
            int size = contour.internal.size();
            if(size > 0 && size < 3)
            {
                ContourBoundingBox bb = new ContourBoundingBox(contour.external);
                double ratio = bb.area()/imgArea;
                if(ratio > 0.08 )
                {
                    bounds.add(bb);
                }
            }
        }
        Graphics g = in.getGraphics();
        for(ContourBoundingBox b:bounds)
        {
            b.draw(g);
        }
        return in;
    }

    @Override
	protected boolean handleClick(java.awt.event.MouseEvent e) {
		return false;
	}
}