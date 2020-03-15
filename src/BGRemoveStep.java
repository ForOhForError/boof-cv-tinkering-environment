import java.awt.Image;
import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

import boofcv.alg.InputSanityCheck;
import boofcv.alg.background.BackgroundModelStationary;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.factory.background.ConfigBackgroundBasic;
import boofcv.factory.background.FactoryBackgroundModel;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;

class BGRemoveStep extends ProcessStep
{

    ImageType<GrayU8> imageType = ImageType.single(GrayU8.class);
    BackgroundModelStationary<GrayU8> background =
		FactoryBackgroundModel.stationaryBasic(new ConfigBackgroundBasic(60, 0.01f), imageType);
    private Webcam w;

    public BGRemoveStep(Webcam w)
    {
        this.w = w;
    }

    @Override
    protected Image process(BufferedImage in) {
        GrayU8 img = ConvertBufferedImage.convertFromSingle(in, null, GrayU8.class);
        GrayU8 binary = img.createSameShape();
        background.segment(img, binary);
        binary=BinaryImageOps.dilate8(binary, 30, null);
        binary=BinaryImageOps.erode8(binary, 15, null);
        img=mask(img,binary,null);
        return ConvertBufferedImage.convertTo(img, in);
    }

    public GrayU8 mask(GrayU8 source, GrayU8 mask, GrayU8 output)
	{
        InputSanityCheck.checkSameShape(source,mask);
		output = InputSanityCheck.checkDeclare(source, output);
		for( int y = 0; y < source.height; y++ ) {
			int indexA = source.startIndex + y*source.stride;
			int indexB = mask.startIndex + y*mask.stride;
			int indexOut = output.startIndex + y*output.stride;

			int end = indexA + source.width;
			for( ; indexA < end; indexA++,indexB++,indexOut++) {
                byte srcval = source.data[indexA];
                byte mskval = mask.data[indexB];
				output.data[indexOut] = mskval == (byte)1 ? srcval:(byte)0;
			}
        }
        return output;
    }

    public GrayU8 amp(GrayU8 mask, GrayU8 output)
	{
		output = InputSanityCheck.checkDeclare(mask, output);
		for( int y = 0; y < mask.height; y++ ) {
			int indexA = mask.startIndex + y*mask.stride;
			int indexOut = output.startIndex + y*output.stride;

			int end = indexA + mask.width;
			for( ; indexA < end; indexA++,indexOut++) {
				output.data[indexOut] = (byte)(mask.data[indexA]*255);
			}
        }
        return output;
    }
    
    public void adaptBackground()
    {
        background.reset();
        BufferedImage frame = w.getImage();
        GrayU8 img = null;
        for(int i=0;i<10;i++)
        {
            img = ConvertBufferedImage.convertFromSingle(frame, img, GrayU8.class);
            background.updateBackground(img);
            frame = w.getImage();
        }
    }

    @Override
	protected boolean handleClick(java.awt.event.MouseEvent e) {
        adaptBackground();
        return true;
	}
}