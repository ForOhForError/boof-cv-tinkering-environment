import java.awt.Image;
import java.awt.image.BufferedImage;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;

class HistogramEqualizeStep extends ProcessStep
{

    int histogram[] = new int[256];
    int transform[] = new int[256];

    @Override
    protected Image process(BufferedImage in) {
        GrayU8 img = ConvertBufferedImage.convertFromSingle(in, null, GrayU8.class);
        GrayU8 norm = img.createSameShape();
        ImageStatistics.histogram(img,0,histogram);
        EnhanceImageOps.equalize(histogram, transform);
        EnhanceImageOps.applyTransform(img, transform, norm);
        return ConvertBufferedImage.convertTo(norm, in);
    }

    @Override
	protected boolean handleClick(java.awt.event.MouseEvent e) {
		return false;
	}
}