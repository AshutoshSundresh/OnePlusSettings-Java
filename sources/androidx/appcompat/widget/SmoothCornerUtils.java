package androidx.appcompat.widget;

import android.graphics.Path;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.List;

public class SmoothCornerUtils {
    public static List<Path> calculateBezierCornerPaths(RectF rectF, float f) {
        ArrayList arrayList = new ArrayList();
        float f2 = rectF.right;
        float f3 = rectF.left;
        float f4 = f2 - f3;
        float f5 = rectF.bottom;
        float f6 = rectF.top;
        float f7 = f5 - f6;
        if (f4 > 0.0f && f7 > 0.0f && f > 0.0f) {
            float f8 = f4 / 2.0f;
            float f9 = f7 / 2.0f;
            float min = f / Math.min(f8, f9);
            float f10 = 1.0f;
            float min2 = min > 0.5f ? 1.0f - (Math.min(1.0f, (min - 0.5f) / 0.4f) * 0.13877845f) : 1.0f;
            if (min > 0.6f) {
                f10 = 1.0f + (Math.min(1.0f, (min - 0.6f) / 0.3f) * 0.042454004f);
            }
            float f11 = min2 * (f / 100.0f) * 128.19f;
            Path path = new Path();
            float f12 = f4 - f11;
            path.moveTo(f3 + Math.max(f8, f12), f6);
            float f13 = f4 + f3;
            float f14 = 0.8362f * f * f10;
            float f15 = f13 - f14;
            float f16 = 0.0f * f;
            float f17 = f6 + f16;
            float f18 = 0.6745f * f;
            float f19 = f13 - f18;
            float f20 = 0.0464f * f;
            float f21 = f6 + f20;
            float f22 = 0.5116f * f;
            float f23 = f13 - f22;
            float f24 = 0.1336f * f;
            float f25 = f6 + f24;
            path.cubicTo(f15, f17, f19, f21, f23, f25);
            float f26 = 0.3486f * f;
            float f27 = f13 - f26;
            float f28 = f * 0.2207f;
            float f29 = f6 + f28;
            float f30 = f13 - f28;
            float f31 = f6 + f26;
            float f32 = f13 - f24;
            float f33 = f6 + f22;
            path.cubicTo(f27, f29, f30, f31, f32, f33);
            float f34 = f13 - f20;
            float f35 = f6 + f18;
            float f36 = f13 - f16;
            float f37 = f6 + f14;
            path.cubicTo(f34, f35, f36, f37, f13, f6 + Math.min(f9, f11));
            path.lineTo(f2 + Math.max(f8, f12), f6 - Math.max(f8, f12));
            path.close();
            arrayList.add(path);
            Path path2 = new Path();
            float f38 = f7 - f11;
            path2.moveTo(f13, Math.max(f9, f38) + f6);
            float f39 = f7 + f6;
            float f40 = f39 - f14;
            float f41 = f39 - f18;
            float f42 = f39 - f22;
            path2.cubicTo(f36, f40, f34, f41, f32, f42);
            float f43 = f39 - f26;
            float f44 = f39 - f28;
            float f45 = f39 - f24;
            path2.cubicTo(f30, f43, f27, f44, f23, f45);
            float f46 = f39 - f20;
            float f47 = f39 - f16;
            path2.cubicTo(f19, f46, f15, f47, f3 + Math.max(f8, f12), f39);
            path2.lineTo(f2 + Math.max(f9, f38), f5 + Math.max(f9, f38));
            path2.close();
            arrayList.add(path2);
            Path path3 = new Path();
            path3.moveTo(Math.min(f8, f11) + f3, f39);
            float f48 = f3 + f14;
            float f49 = f3 + f18;
            float f50 = f3 + f22;
            path3.cubicTo(f48, f47, f49, f46, f50, f45);
            float f51 = f3 + f26;
            float f52 = f3 + f28;
            float f53 = f3 + f24;
            path3.cubicTo(f51, f44, f52, f43, f53, f42);
            float f54 = f3 + f20;
            float f55 = f3 + f16;
            path3.cubicTo(f54, f41, f55, f40, f3, f6 + Math.max(f9, f38));
            path3.lineTo(f3 - Math.min(f8, f11), f5 + Math.min(f9, f11));
            path3.close();
            arrayList.add(path3);
            Path path4 = new Path();
            path4.moveTo(f3, Math.min(f9, f11) + f6);
            path4.cubicTo(f55, f37, f54, f35, f53, f33);
            path4.cubicTo(f52, f31, f51, f29, f50, f25);
            path4.cubicTo(f49, f21, f48, f17, Math.min(f8, f11) + f3, f6);
            path4.lineTo(f3 - Math.min(f9, f11), f6 - Math.min(f9, f11));
            path4.close();
            arrayList.add(path4);
        }
        return arrayList;
    }
}
