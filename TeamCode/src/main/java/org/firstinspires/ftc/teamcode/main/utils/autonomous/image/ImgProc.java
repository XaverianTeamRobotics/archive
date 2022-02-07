package org.firstinspires.ftc.teamcode.main.utils.autonomous.image;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XZY;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

import java.util.ArrayList;
import java.util.List;

public class ImgProc {
    private static final String OBJECT_TO_IDENT = "Duck";
    private final String VUFORIA_KEY;
    private final String CAMERA_NAME;
    private ArrayList<Detection> Detections;
    public VuforiaLocalizer vuforia;
    public TFObjectDetector tfod;
    public String TFOD_MODEL_ASSET;
    public String[] LABELS;
    public HardwareMap hardwareMap;
    public float confidence = 0.5f;
    boolean initialObjectIdent = false;
    boolean initialObjectIdentSTRICT = false;

    private VuforiaTrackables vuforiaTrackables = null;

    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = 6 * mmPerInch;          // the height of the center of the target image above the floor
    private static final float halfField        = 72 * mmPerInch;
    private static final float halfTile         = 12 * mmPerInch;
    private static final float oneAndHalfTile   = 36 * mmPerInch;

    public List<VuforiaTrackable> allTrackables;
    private ArrayList<InitialPositions> possibleStartingPositions = new ArrayList();;

    public ImgProc(HardwareMap hardwareMap) {
        this(   "AcQbfNb/////AAABmUoZxvy9bUCeksf5rYATLidV6rQS+xwgakOfD4C+LPj4FmsvqtRDFihtnTBZUUxxFbyM7CJMfiYTUEwcDMJERl938oY8iVD43E/SxeO64bOSBfLC0prrE1H4E5SS/IzsVcQCa9GsNaWrTEushMhdoXA3VSaW6R9KrrwvKYdNN/SbaN4TPslQkTqSUr63K60pkE5GqpeadAQuIm8V6LK63JD1TlF665EgpfsDZeVUBeAiJE86iGlT1/vNJ9kisAqKpBHsRyokaVClRnjlp28lmodjVRqeSk8cjCuYryn74tClfxfHQpkDDIsJO+7IYwJQCZQZZ+U9KJaMUeben4HOj0JTnQaEE6MZLaLQzY+C/6MS",
                "FreightFrenzy_BC.tflite",
                new String[]{"Ball", "Cube"},
                hardwareMap,
                Resources.Misc.Webcam
        );
    }

    public ImgProc(HardwareMap hardwareMap, String[] objectsToDetect, String file) {
        this(   "AcQbfNb/////AAABmUoZxvy9bUCeksf5rYATLidV6rQS+xwgakOfD4C+LPj4FmsvqtRDFihtnTBZUUxxFbyM7CJMfiYTUEwcDMJERl938oY8iVD43E/SxeO64bOSBfLC0prrE1H4E5SS/IzsVcQCa9GsNaWrTEushMhdoXA3VSaW6R9KrrwvKYdNN/SbaN4TPslQkTqSUr63K60pkE5GqpeadAQuIm8V6LK63JD1TlF665EgpfsDZeVUBeAiJE86iGlT1/vNJ9kisAqKpBHsRyokaVClRnjlp28lmodjVRqeSk8cjCuYryn74tClfxfHQpkDDIsJO+7IYwJQCZQZZ+U9KJaMUeben4HOj0JTnQaEE6MZLaLQzY+C/6MS",
                file,
                objectsToDetect,
                hardwareMap,
                Resources.Misc.Webcam
        );
    }

    public ImgProc(String VUFORIA_KEY, String TFOD_MODEL_ASSET, String[] LABELS, HardwareMap hardwareMap, String cameraName) {
        this.VUFORIA_KEY = VUFORIA_KEY;
        this.TFOD_MODEL_ASSET = TFOD_MODEL_ASSET;
        this.LABELS = LABELS;
        this.hardwareMap = hardwareMap;
        this.CAMERA_NAME = cameraName;
    }

    public void init() {
        initVuforia();
        initTFOD();
    }

    public void setZoom(double magnification, double aspectRatio) {
        tfod.setZoom(magnification, aspectRatio);
    }

    public void activate() {
        tfod.activate();
        vuforiaTrackables.activate();
    }

    public List<Recognition> getUpdatedRecognitions() {
        return tfod.getUpdatedRecognitions();
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, CAMERA_NAME);
        parameters.useExtendedTracking = false;
        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        vuforiaTrackables = this.vuforia.loadTrackablesFromAsset("FreightFrenzy");

        allTrackables = new ArrayList<VuforiaTrackable>(vuforiaTrackables);

        identifyTarget(0, "Blue Storage",       -halfField,  oneAndHalfTile, mmTargetHeight, 90, 0, 90);
        identifyTarget(1, "Blue Alliance Wall",  halfTile,   halfField,      mmTargetHeight, 90, 0, 0);
        identifyTarget(2, "Red Storage",        -halfField, -oneAndHalfTile, mmTargetHeight, 90, 0, 90);
        identifyTarget(3, "Red Alliance Wall",   halfTile,  -halfField,      mmTargetHeight, 90, 0, 180);

        final float CAMERA_FORWARD_DISPLACEMENT  = 6.0f * mmPerInch;
        final float CAMERA_VERTICAL_DISPLACEMENT = 6.0f * mmPerInch;
        final float CAMERA_LEFT_DISPLACEMENT     = -6.0f * mmPerInch;

        OpenGLMatrix cameraLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XZY, DEGREES, 90, 90, 0));

        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setCameraLocationOnRobot(parameters.cameraName, cameraLocationOnRobot);
        }


    }

    private void initTFOD() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = confidence;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);

        possibleStartingPositions.add(InitialPositions.POS1);
        possibleStartingPositions.add(InitialPositions.POS2);
        possibleStartingPositions.add(InitialPositions.POS3);
    }

    public ArrayList<Detection> getDetections() {
        return Detections;
    }

    public void clearDetections() {
        Detections.clear();
    }

    public void registerDetection(Detection detection) {
        Detections.add(detection);
    }

    public ArrayList<Detection> searchDetections(String name) {
        ArrayList<Detection> returnList = new ArrayList();
        for (Detection detection : Detections) {
            if (detection.friendlyName.equals(name)) {
                returnList.add(detection);
            }
        }
        return returnList;
    }

    public static class Detection {
        public Detection(float x, float y, float imageHeight, float imageWidth, String friendlyName, float angle) {
            this.x = x;
            this.y = y;
            this.imageHeight = imageHeight;
            this.imageWidth = imageWidth;
            this.friendlyName = friendlyName;
            this.angle = angle;
        }

        public float x;
        public float y;
        public float imageHeight;
        public float imageWidth;
        public String friendlyName;
        public float angle;
    }

    public static class VuforiaLocationInfo {
        public OpenGLMatrix location;
        public VectorF translation;
        public String trackableName;

        public VuforiaLocationInfo(OpenGLMatrix location, VectorF translation, String trackableName) {
            this.location = location;
            this.translation = translation;
            this.trackableName = trackableName;
        }
    }

    public VuforiaLocationInfo getVisibleVuforiaTarget() {
        VuforiaLocationInfo toReturn = new VuforiaLocationInfo(null, null, null);

        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {

                toReturn.trackableName = trackable.getName();

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    toReturn.location = robotLocationTransform;
                    toReturn.translation = robotLocationTransform.getTranslation();
                }
                break;
            }
        }
        return toReturn;
    }

    public int identifyStartingPos() {
        if (this.tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = this.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                int i = 0;
                for (Recognition recognition : updatedRecognitions) {
                    i++;
                    if (!initialObjectIdent || !initialObjectIdentSTRICT) {
                        if (InitialPositions.POS1.evalPos((int) recognition.getLeft())) {
                            if (recognition.getLabel().equals(OBJECT_TO_IDENT)) {
                                possibleStartingPositions = new ArrayList();
                                possibleStartingPositions.add(InitialPositions.POS1);
                                initialObjectIdentSTRICT = true;
                            } else possibleStartingPositions.remove(InitialPositions.POS1);
                        } else if (InitialPositions.POS2.evalPos((int) recognition.getLeft())) {
                            if (recognition.getLabel().equals(OBJECT_TO_IDENT)) {
                                possibleStartingPositions = new ArrayList();
                                possibleStartingPositions.add(InitialPositions.POS2);
                                initialObjectIdentSTRICT = true;
                            } else possibleStartingPositions.remove(InitialPositions.POS2);
                        } else if (InitialPositions.POS3.evalPos((int) recognition.getLeft())) {
                            if (recognition.getLabel().equals(OBJECT_TO_IDENT)) {
                                possibleStartingPositions = new ArrayList();
                                possibleStartingPositions.add(InitialPositions.POS3);
                                initialObjectIdentSTRICT = true;
                            } else possibleStartingPositions.remove(InitialPositions.POS3);
                        }
                    }
                }
                if (possibleStartingPositions.size() == 1) {
                    initialObjectIdent = true;
                } else initialObjectIdent = false;
            }
        }

        int t = 0;

        if (initialObjectIdent || initialObjectIdentSTRICT) {
            switch (possibleStartingPositions.get(0)) {
                case POS1:
                    t = 1;
                    break;
                case POS2:
                    t = 2;
                    break;
                case POS3:
                    t = 3;
                    break;
            }
        }

        return t;
    }

    void identifyTarget(int targetIndex, String targetName, float dx, float dy, float dz, float rx, float ry, float rz) {
        VuforiaTrackable aTarget = vuforiaTrackables.get(targetIndex);
        aTarget.setName(targetName);
        aTarget.setLocation(OpenGLMatrix.translation(dx, dy, dz)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, rx, ry, rz)));
    }
}