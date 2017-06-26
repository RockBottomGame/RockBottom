package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimplexNoise implements INoiseGen{

    private static final Grad[] GRAD_3 = {
            new Grad(1, 1, 0), new Grad(-1, 1, 0), new Grad(1, -1, 0), new Grad(-1, -1, 0),
            new Grad(1, 0, 1), new Grad(-1, 0, 1), new Grad(1, 0, -1), new Grad(-1, 0, -1),
            new Grad(0, 1, 1), new Grad(0, -1, 1), new Grad(0, 1, -1), new Grad(0, -1, -1)};

    private static final Grad[] GRAD_4 = {
            new Grad(0, 1, 1, 1), new Grad(0, 1, 1, -1), new Grad(0, 1, -1, 1), new Grad(0, 1, -1, -1),
            new Grad(0, -1, 1, 1), new Grad(0, -1, 1, -1), new Grad(0, -1, -1, 1), new Grad(0, -1, -1, -1),
            new Grad(1, 0, 1, 1), new Grad(1, 0, 1, -1), new Grad(1, 0, -1, 1), new Grad(1, 0, -1, -1),
            new Grad(-1, 0, 1, 1), new Grad(-1, 0, 1, -1), new Grad(-1, 0, -1, 1), new Grad(-1, 0, -1, -1),
            new Grad(1, 1, 0, 1), new Grad(1, 1, 0, -1), new Grad(1, -1, 0, 1), new Grad(1, -1, 0, -1),
            new Grad(-1, 1, 0, 1), new Grad(-1, 1, 0, -1), new Grad(-1, -1, 0, 1), new Grad(-1, -1, 0, -1),
            new Grad(1, 1, 1, 0), new Grad(1, 1, -1, 0), new Grad(1, -1, 1, 0), new Grad(1, -1, -1, 0),
            new Grad(-1, 1, 1, 0), new Grad(-1, 1, -1, 0), new Grad(-1, -1, 1, 0), new Grad(-1, -1, -1, 0)};

    private final int[] perm = new int[512];
    private final int[] perm12 = new int[512];

    public SimplexNoise(Random random){
        List<Integer> permutations = new ArrayList<>();
        for(int i = 0; i < 256; i++){
            permutations.add(i);
        }
        Collections.shuffle(permutations, random);

        for(int i = 0; i < 512; i++){
            this.perm[i] = permutations.get(i & 255);
            this.perm12[i] = this.perm[i]%12;
        }
    }

    private static final double F2 = 0.5*(Math.sqrt(3)-1);
    private static final double G2 = (3-Math.sqrt(3))/6;
    private static final double F3 = 1/3;
    private static final double G3 = 1/6;
    private static final double F4 = (Math.sqrt(5)-1)/4;
    private static final double G4 = (5-Math.sqrt(5))/20;

    private static double dot(Grad g, double x, double y){
        return g.x*x+g.y*y;
    }

    private static double dot(Grad g, double x, double y, double z){
        return g.x*x+g.y*y+g.z*z;
    }

    private static double dot(Grad g, double x, double y, double z, double w){
        return g.x*x+g.y*y+g.z*z+g.w*w;
    }

    @Override
    public double make2dNoise(double x, double y){
        double n0, n1, n2;

        double s = (x+y)*F2;
        int i = Util.floor(x+s);
        int j = Util.floor(y+s);
        double t = (i+j)*G2;

        double x0 = x-(i-t);
        double y0 = y-(j-t);

        int i1, j1;
        if(x0 > y0){
            i1 = 1;
            j1 = 0;
        }
        else{
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0-i1+G2;
        double y1 = y0-j1+G2;
        double x2 = x0-1+2*G2;
        double y2 = y0-1+2*G2;

        int ii = i & 255;
        int jj = j & 255;
        int gi0 = this.perm12[ii+this.perm[jj]];
        int gi1 = this.perm12[ii+i1+this.perm[jj+j1]];
        int gi2 = this.perm12[ii+1+this.perm[jj+1]];

        double t0 = 0.5-x0*x0-y0*y0;
        if(t0 < 0){
            n0 = 0;
        }
        else{
            t0 *= t0;
            n0 = t0*t0*dot(GRAD_3[gi0], x0, y0);
        }
        double t1 = 0.5-x1*x1-y1*y1;
        if(t1 < 0){
            n1 = 0;
        }
        else{
            t1 *= t1;
            n1 = t1*t1*dot(GRAD_3[gi1], x1, y1);
        }
        double t2 = 0.5-x2*x2-y2*y2;
        if(t2 < 0){
            n2 = 0;
        }
        else{
            t2 *= t2;
            n2 = t2*t2*dot(GRAD_3[gi2], x2, y2);
        }

        return 70*(n0+n1+n2);
    }

    @Override
    public double make3dNoise(double x, double y, double z){
        double n0, n1, n2, n3;

        double s = (x+y+z)*F3;
        int i = Util.floor(x+s);
        int j = Util.floor(y+s);
        int k = Util.floor(z+s);
        double t = (i+j+k)*G3;

        double x0 = x-(i-t);
        double y0 = y-(j-t);
        double z0 = z-(k-t);

        int i1, j1, k1;
        int i2, j2, k2;
        if(x0 >= y0){
            if(y0 >= z0){
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
            else if(x0 >= z0){
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
            else{
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        }
        else{
            if(y0 < z0){
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            }
            else if(x0 < z0){
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            }
            else{
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }

        double x1 = x0-i1+G3;
        double y1 = y0-j1+G3;
        double z1 = z0-k1+G3;
        double x2 = x0-i2+2*G3;
        double y2 = y0-j2+2*G3;
        double z2 = z0-k2+2*G3;
        double x3 = x0-1+3*G3;
        double y3 = y0-1+3*G3;
        double z3 = z0-1+3*G3;

        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int gi0 = this.perm12[ii+this.perm[jj+this.perm[kk]]];
        int gi1 = this.perm12[ii+i1+this.perm[jj+j1+this.perm[kk+k1]]];
        int gi2 = this.perm12[ii+i2+this.perm[jj+j2+this.perm[kk+k2]]];
        int gi3 = this.perm12[ii+1+this.perm[jj+1+this.perm[kk+1]]];

        double t0 = 0.6-x0*x0-y0*y0-z0*z0;
        if(t0 < 0){
            n0 = 0;
        }
        else{
            t0 *= t0;
            n0 = t0*t0*dot(GRAD_3[gi0], x0, y0, z0);
        }
        double t1 = 0.6-x1*x1-y1*y1-z1*z1;
        if(t1 < 0){
            n1 = 0;
        }
        else{
            t1 *= t1;
            n1 = t1*t1*dot(GRAD_3[gi1], x1, y1, z1);
        }
        double t2 = 0.6-x2*x2-y2*y2-z2*z2;
        if(t2 < 0){
            n2 = 0;
        }
        else{
            t2 *= t2;
            n2 = t2*t2*dot(GRAD_3[gi2], x2, y2, z2);
        }
        double t3 = 0.6-x3*x3-y3*y3-z3*z3;
        if(t3 < 0){
            n3 = 0;
        }
        else{
            t3 *= t3;
            n3 = t3*t3*dot(GRAD_3[gi3], x3, y3, z3);
        }

        return 32*(n0+n1+n2+n3);
    }

    @Override
    public double make4dNoise(double x, double y, double z, double w){
        double n0, n1, n2, n3, n4;

        double s = (x+y+z+w)*F4;
        int i = Util.floor(x+s);
        int j = Util.floor(y+s);
        int k = Util.floor(z+s);
        int l = Util.floor(w+s);
        double t = (i+j+k+l)*G4;

        double x0 = x-(i-t);
        double y0 = y-(j-t);
        double z0 = z-(k-t);
        double w0 = w-(l-t);

        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        if(x0 > y0){
            rankx++;
        }
        else{
            ranky++;
        }
        if(x0 > z0){
            rankx++;
        }
        else{
            rankz++;
        }
        if(x0 > w0){
            rankx++;
        }
        else{
            rankw++;
        }
        if(y0 > z0){
            ranky++;
        }
        else{
            rankz++;
        }
        if(y0 > w0){
            ranky++;
        }
        else{
            rankw++;
        }
        if(z0 > w0){
            rankz++;
        }
        else{
            rankw++;
        }
        int i1, j1, k1, l1;
        int i2, j2, k2, l2;
        int i3, j3, k3, l3;

        i1 = rankx >= 3 ? 1 : 0;
        j1 = ranky >= 3 ? 1 : 0;
        k1 = rankz >= 3 ? 1 : 0;
        l1 = rankw >= 3 ? 1 : 0;

        i2 = rankx >= 2 ? 1 : 0;
        j2 = ranky >= 2 ? 1 : 0;
        k2 = rankz >= 2 ? 1 : 0;
        l2 = rankw >= 2 ? 1 : 0;

        i3 = rankx >= 1 ? 1 : 0;
        j3 = ranky >= 1 ? 1 : 0;
        k3 = rankz >= 1 ? 1 : 0;
        l3 = rankw >= 1 ? 1 : 0;

        double x1 = x0-i1+G4;
        double y1 = y0-j1+G4;
        double z1 = z0-k1+G4;
        double w1 = w0-l1+G4;
        double x2 = x0-i2+2*G4;
        double y2 = y0-j2+2*G4;
        double z2 = z0-k2+2*G4;
        double w2 = w0-l2+2*G4;
        double x3 = x0-i3+3*G4;
        double y3 = y0-j3+3*G4;
        double z3 = z0-k3+3*G4;
        double w3 = w0-l3+3*G4;
        double x4 = x0-1+4*G4;
        double y4 = y0-1+4*G4;
        double z4 = z0-1+4*G4;
        double w4 = w0-1+4*G4;

        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int ll = l & 255;
        int gi0 = this.perm[ii+this.perm[jj+this.perm[kk+this.perm[ll]]]]%32;
        int gi1 = this.perm[ii+i1+this.perm[jj+j1+this.perm[kk+k1+this.perm[ll+l1]]]]%32;
        int gi2 = this.perm[ii+i2+this.perm[jj+j2+this.perm[kk+k2+this.perm[ll+l2]]]]%32;
        int gi3 = this.perm[ii+i3+this.perm[jj+j3+this.perm[kk+k3+this.perm[ll+l3]]]]%32;
        int gi4 = this.perm[ii+1+this.perm[jj+1+this.perm[kk+1+this.perm[ll+1]]]]%32;

        double t0 = 0.6-x0*x0-y0*y0-z0*z0-w0*w0;
        if(t0 < 0){
            n0 = 0;
        }
        else{
            t0 *= t0;
            n0 = t0*t0*dot(GRAD_4[gi0], x0, y0, z0, w0);
        }
        double t1 = 0.6-x1*x1-y1*y1-z1*z1-w1*w1;
        if(t1 < 0){
            n1 = 0;
        }
        else{
            t1 *= t1;
            n1 = t1*t1*dot(GRAD_4[gi1], x1, y1, z1, w1);
        }
        double t2 = 0.6-x2*x2-y2*y2-z2*z2-w2*w2;
        if(t2 < 0){
            n2 = 0;
        }
        else{
            t2 *= t2;
            n2 = t2*t2*dot(GRAD_4[gi2], x2, y2, z2, w2);
        }
        double t3 = 0.6-x3*x3-y3*y3-z3*z3-w3*w3;
        if(t3 < 0){
            n3 = 0;
        }
        else{
            t3 *= t3;
            n3 = t3*t3*dot(GRAD_4[gi3], x3, y3, z3, w3);
        }
        double t4 = 0.6-x4*x4-y4*y4-z4*z4-w4*w4;
        if(t4 < 0){
            n4 = 0;
        }
        else{
            t4 *= t4;
            n4 = t4*t4*dot(GRAD_4[gi4], x4, y4, z4, w4);
        }
        return 27*(n0+n1+n2+n3+n4);
    }

    private static class Grad{

        private final double x;
        private final double y;
        private final double z;
        private final double w;

        private Grad(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = 0;
        }

        private Grad(double x, double y, double z, double w){
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
    }
}
