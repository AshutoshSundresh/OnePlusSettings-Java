package androidx.constraintlayout.motion.utils;

import androidx.constraintlayout.widget.R$styleable;
import java.util.Arrays;

/* access modifiers changed from: package-private */
public class ArcCurveFit extends CurveFit {
    Arc[] mArcs;
    private final double[] mTime;

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double d, double[] dArr) {
        Arc[] arcArr = this.mArcs;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        }
        Arc[] arcArr2 = this.mArcs;
        if (d > arcArr2[arcArr2.length - 1].mTime2) {
            d = arcArr2[arcArr2.length - 1].mTime2;
        }
        int i = 0;
        while (true) {
            Arc[] arcArr3 = this.mArcs;
            if (i >= arcArr3.length) {
                return;
            }
            if (d > arcArr3[i].mTime2) {
                i++;
            } else if (arcArr3[i].linear) {
                dArr[0] = arcArr3[i].getLinearX(d);
                dArr[1] = this.mArcs[i].getLinearY(d);
                return;
            } else {
                arcArr3[i].setPoint(d);
                dArr[0] = this.mArcs[i].getX();
                dArr[1] = this.mArcs[i].getY();
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double d, float[] fArr) {
        Arc[] arcArr = this.mArcs;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        } else if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        int i = 0;
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i >= arcArr2.length) {
                return;
            }
            if (d > arcArr2[i].mTime2) {
                i++;
            } else if (arcArr2[i].linear) {
                fArr[0] = (float) arcArr2[i].getLinearX(d);
                fArr[1] = (float) this.mArcs[i].getLinearY(d);
                return;
            } else {
                arcArr2[i].setPoint(d);
                fArr[0] = (float) this.mArcs[i].getX();
                fArr[1] = (float) this.mArcs[i].getY();
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getSlope(double d, double[] dArr) {
        Arc[] arcArr = this.mArcs;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        } else if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        int i = 0;
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i >= arcArr2.length) {
                return;
            }
            if (d > arcArr2[i].mTime2) {
                i++;
            } else if (arcArr2[i].linear) {
                dArr[0] = arcArr2[i].getLinearDX(d);
                dArr[1] = this.mArcs[i].getLinearDY(d);
                return;
            } else {
                arcArr2[i].setPoint(d);
                dArr[0] = this.mArcs[i].getDX();
                dArr[1] = this.mArcs[i].getDY();
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getPos(double d, int i) {
        Arc[] arcArr = this.mArcs;
        int i2 = 0;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        } else if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i2 >= arcArr2.length) {
                return Double.NaN;
            }
            if (d > arcArr2[i2].mTime2) {
                i2++;
            } else if (!arcArr2[i2].linear) {
                arcArr2[i2].setPoint(d);
                if (i == 0) {
                    return this.mArcs[i2].getX();
                }
                return this.mArcs[i2].getY();
            } else if (i == 0) {
                return arcArr2[i2].getLinearX(d);
            } else {
                return arcArr2[i2].getLinearY(d);
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getSlope(double d, int i) {
        Arc[] arcArr = this.mArcs;
        int i2 = 0;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        }
        Arc[] arcArr2 = this.mArcs;
        if (d > arcArr2[arcArr2.length - 1].mTime2) {
            d = arcArr2[arcArr2.length - 1].mTime2;
        }
        while (true) {
            Arc[] arcArr3 = this.mArcs;
            if (i2 >= arcArr3.length) {
                return Double.NaN;
            }
            if (d > arcArr3[i2].mTime2) {
                i2++;
            } else if (!arcArr3[i2].linear) {
                arcArr3[i2].setPoint(d);
                if (i == 0) {
                    return this.mArcs[i2].getDX();
                }
                return this.mArcs[i2].getDY();
            } else if (i == 0) {
                return arcArr3[i2].getLinearDX(d);
            } else {
                return arcArr3[i2].getLinearDY(d);
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double[] getTimePoints() {
        return this.mTime;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        if (r5 == 1) goto L_0x002a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ArcCurveFit(int[] r27, double[] r28, double[][] r29) {
        /*
            r26 = this;
            r0 = r26
            r1 = r28
            r26.<init>()
            r0.mTime = r1
            int r2 = r1.length
            r3 = 1
            int r2 = r2 - r3
            androidx.constraintlayout.motion.utils.ArcCurveFit$Arc[] r2 = new androidx.constraintlayout.motion.utils.ArcCurveFit.Arc[r2]
            r0.mArcs = r2
            r2 = 0
            r4 = r2
            r5 = r3
            r6 = r5
        L_0x0014:
            androidx.constraintlayout.motion.utils.ArcCurveFit$Arc[] r7 = r0.mArcs
            int r7 = r7.length
            if (r4 >= r7) goto L_0x0061
            r7 = r27[r4]
            r8 = 3
            r9 = 2
            if (r7 == 0) goto L_0x0030
            if (r7 == r3) goto L_0x002c
            if (r7 == r9) goto L_0x002a
            if (r7 == r8) goto L_0x0028
            r20 = r6
            goto L_0x0032
        L_0x0028:
            if (r5 != r3) goto L_0x002c
        L_0x002a:
            r5 = r9
            goto L_0x002d
        L_0x002c:
            r5 = r3
        L_0x002d:
            r20 = r5
            goto L_0x0032
        L_0x0030:
            r20 = r8
        L_0x0032:
            androidx.constraintlayout.motion.utils.ArcCurveFit$Arc[] r14 = r0.mArcs
            androidx.constraintlayout.motion.utils.ArcCurveFit$Arc r21 = new androidx.constraintlayout.motion.utils.ArcCurveFit$Arc
            r8 = r1[r4]
            int r22 = r4 + 1
            r10 = r1[r22]
            r6 = r29[r4]
            r12 = r6[r2]
            r6 = r29[r4]
            r15 = r6[r3]
            r6 = r29[r22]
            r17 = r6[r2]
            r6 = r29[r22]
            r23 = r6[r3]
            r6 = r21
            r7 = r20
            r25 = r14
            r14 = r15
            r16 = r17
            r18 = r23
            r6.<init>(r7, r8, r10, r12, r14, r16, r18)
            r25[r4] = r21
            r6 = r20
            r4 = r22
            goto L_0x0014
        L_0x0061:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.motion.utils.ArcCurveFit.<init>(int[], double[], double[][]):void");
    }

    private static class Arc {
        private static double[] ourPercent = new double[91];
        boolean linear = false;
        double mArcDistance;
        double mArcVelocity;
        double mEllipseA;
        double mEllipseB;
        double mEllipseCenterX;
        double mEllipseCenterY;
        double[] mLut;
        double mOneOverDeltaTime;
        double mTime1;
        double mTime2;
        double mTmpCosAngle;
        double mTmpSinAngle;
        boolean mVertical;
        double mX1;
        double mX2;
        double mY1;
        double mY2;

        Arc(int i, double d, double d2, double d3, double d4, double d5, double d6) {
            boolean z = false;
            this.mVertical = i == 1 ? true : z;
            this.mTime1 = d;
            this.mTime2 = d2;
            this.mOneOverDeltaTime = 1.0d / (d2 - d);
            if (3 == i) {
                this.linear = true;
            }
            double d7 = d5 - d3;
            double d8 = d6 - d4;
            if (this.linear || Math.abs(d7) < 0.001d || Math.abs(d8) < 0.001d) {
                this.linear = true;
                this.mX1 = d3;
                this.mX2 = d5;
                this.mY1 = d4;
                this.mY2 = d6;
                double hypot = Math.hypot(d8, d7);
                this.mArcDistance = hypot;
                this.mArcVelocity = hypot * this.mOneOverDeltaTime;
                double d9 = this.mTime2;
                double d10 = this.mTime1;
                this.mEllipseCenterX = d7 / (d9 - d10);
                this.mEllipseCenterY = d8 / (d9 - d10);
                return;
            }
            this.mLut = new double[R$styleable.Constraint_layout_goneMarginRight];
            this.mEllipseA = d7 * ((double) (this.mVertical ? -1 : 1));
            this.mEllipseB = d8 * ((double) (this.mVertical ? 1 : -1));
            this.mEllipseCenterX = this.mVertical ? d5 : d3;
            this.mEllipseCenterY = this.mVertical ? d4 : d6;
            buildTable(d3, d4, d5, d6);
            this.mArcVelocity = this.mArcDistance * this.mOneOverDeltaTime;
        }

        /* access modifiers changed from: package-private */
        public void setPoint(double d) {
            double lookup = lookup((this.mVertical ? this.mTime2 - d : d - this.mTime1) * this.mOneOverDeltaTime) * 1.5707963267948966d;
            this.mTmpSinAngle = Math.sin(lookup);
            this.mTmpCosAngle = Math.cos(lookup);
        }

        /* access modifiers changed from: package-private */
        public double getX() {
            return this.mEllipseCenterX + (this.mEllipseA * this.mTmpSinAngle);
        }

        /* access modifiers changed from: package-private */
        public double getY() {
            return this.mEllipseCenterY + (this.mEllipseB * this.mTmpCosAngle);
        }

        /* access modifiers changed from: package-private */
        public double getDX() {
            double d = this.mEllipseA * this.mTmpCosAngle;
            double hypot = this.mArcVelocity / Math.hypot(d, (-this.mEllipseB) * this.mTmpSinAngle);
            if (this.mVertical) {
                d = -d;
            }
            return d * hypot;
        }

        /* access modifiers changed from: package-private */
        public double getDY() {
            double d = this.mEllipseA * this.mTmpCosAngle;
            double d2 = (-this.mEllipseB) * this.mTmpSinAngle;
            double hypot = this.mArcVelocity / Math.hypot(d, d2);
            return this.mVertical ? (-d2) * hypot : d2 * hypot;
        }

        public double getLinearX(double d) {
            double d2 = (d - this.mTime1) * this.mOneOverDeltaTime;
            double d3 = this.mX1;
            return d3 + (d2 * (this.mX2 - d3));
        }

        public double getLinearY(double d) {
            double d2 = (d - this.mTime1) * this.mOneOverDeltaTime;
            double d3 = this.mY1;
            return d3 + (d2 * (this.mY2 - d3));
        }

        public double getLinearDX(double d) {
            return this.mEllipseCenterX;
        }

        public double getLinearDY(double d) {
            return this.mEllipseCenterY;
        }

        /* access modifiers changed from: package-private */
        public double lookup(double d) {
            if (d <= 0.0d) {
                return 0.0d;
            }
            if (d >= 1.0d) {
                return 1.0d;
            }
            double[] dArr = this.mLut;
            double length = d * ((double) (dArr.length - 1));
            int i = (int) length;
            return dArr[i] + ((length - ((double) i)) * (dArr[i + 1] - dArr[i]));
        }

        private void buildTable(double d, double d2, double d3, double d4) {
            Arc arc = this;
            double[] dArr = ourPercent;
            double d5 = d3 - d;
            double d6 = d2 - d4;
            int i = 0;
            double d7 = 0.0d;
            double d8 = 0.0d;
            double d9 = 0.0d;
            while (i < dArr.length) {
                double radians = Math.toRadians((((double) i) * 90.0d) / ((double) (dArr.length - 1)));
                double sin = Math.sin(radians) * d5;
                double cos = Math.cos(radians) * d6;
                if (i > 0) {
                    d7 += Math.hypot(sin - d8, cos - d9);
                    dArr[i] = d7;
                }
                i++;
                d9 = cos;
                d8 = sin;
                dArr = dArr;
                arc = this;
            }
            arc.mArcDistance = d7;
            for (int i2 = 0; i2 < dArr.length; i2++) {
                dArr[i2] = dArr[i2] / d7;
            }
            int i3 = 0;
            while (true) {
                double[] dArr2 = arc.mLut;
                if (i3 < dArr2.length) {
                    double length = ((double) i3) / ((double) (dArr2.length - 1));
                    int binarySearch = Arrays.binarySearch(dArr, length);
                    if (binarySearch >= 0) {
                        arc.mLut[i3] = (double) (binarySearch / (dArr.length - 1));
                    } else if (binarySearch == -1) {
                        arc.mLut[i3] = 0.0d;
                    } else {
                        int i4 = -binarySearch;
                        int i5 = i4 - 2;
                        arc.mLut[i3] = (((double) i5) + ((length - dArr[i5]) / (dArr[i4 - 1] - dArr[i5]))) / ((double) (dArr.length - 1));
                    }
                    i3++;
                } else {
                    return;
                }
            }
        }
    }
}
