class ExampleApp
{
    public static void main(String[] args)
	{
        TinkerApp app = new TinkerApp();
        //app.addStep(new HistogramEqualizeStep());
        //app.addStep(new ContourBoxStep());
        //app.addStep(new HistogramEqualizeStep());
        //app.addStep(new BinaryThreshStep());
        //app.addStep(new HoughLineStep());
        //app.addStep(new RansacLineSegmentStep());
        BGRemoveStep bg = new BGRemoveStep(app.getWebcam());
        bg.adaptBackground();
        app.addStep(bg);
        app.addStep(new BinaryThreshStep());
        app.addStep(new ContourBoxStep());
		app.run();
	}
}