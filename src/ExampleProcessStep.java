import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

class ExampleProcessStep extends ProcessStep {

    public ExampleProcessStep(Dimension inputDim) {
    }

    @Override
    protected Image process(BufferedImage in) {
        return in.getScaledInstance(
            outputDim.width,
            outputDim.height,
            BufferedImage.SCALE_FAST
        );
    }

    @Override
    public void updateOutputDim(Dimension d)
    {
        this.outputDim = new Dimension(d.width/2,d.height/2);
    }

}