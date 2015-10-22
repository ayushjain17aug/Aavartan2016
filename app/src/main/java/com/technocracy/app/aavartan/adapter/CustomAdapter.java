package com.technocracy.app.aavartan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.activity.VigyaanActivity;
import com.technocracy.app.aavartan.onlyIntent.ViewPDF;

/**
 * Created by nsn on 8/25/2015.
 */
public class CustomAdapter extends BaseAdapter {

    String ArchiPDF = "http://aavartan.org/archi.pdf";
    String BioMedPDF = "http://aavartan.org/biomed.pdf";
    String BioTechPDF = "http://aavartan.org/biotech.pdf";
    String ChemPDF = "http://aavartan.org/chemical.pdf";
    String CivilPDF = "http://aavartan.org/civil.pdf";
    String CSEPDF = "http://aavartan.org/cse.pdf";
    String ElecPDF = "http://aavartan.org/electrical.pdf";
    String ElexPDF = "http://aavartan.org/etc.pdf";
    String ITPDF = "http://aavartan.org/IT.pdf";
    String MechPDF = "http://aavartan.org/mech.pdf";
    String MetaPDF = "http://aavartan.org/meta.pdf";
    String MiningPDF = "http://aavartan.org/mining.pdf";
    String MCAPDF = "http://aavartan.org/mca.pdf";
    String EcellPDF = "http://aavartan.org/e-cell.pdf";
    String GoGreenPDF = "http://aavartan.org/green.pdf";

    String [] result;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public CustomAdapter(VigyaanActivity mainActivity, String[] prgmNameList, int[] prgmImages) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.problemlist, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);

        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
                switch (position) {
                    case 0:
                        VigyaanActivity.LinkPDF = ArchiPDF;
                        Intent i1 = new Intent(context, ViewPDF.class);
                        context.startActivity(i1);
                        break;
                    case 1:
                        VigyaanActivity.LinkPDF = BioMedPDF;
                        Intent i2 = new Intent(context, ViewPDF.class);
                        context.startActivity(i2);
                        break;
                    case 2:
                        VigyaanActivity.LinkPDF = BioTechPDF;
                        Intent i3 = new Intent(context, ViewPDF.class);
                        context.startActivity(i3);
                        break;
                    case 3:
                        VigyaanActivity.LinkPDF = ChemPDF;
                        Intent i4 = new Intent(context, ViewPDF.class);
                        context.startActivity(i4);
                        break;
                    case 4:
                        VigyaanActivity.LinkPDF = CivilPDF;
                        Intent i5 = new Intent(context, ViewPDF.class);
                        context.startActivity(i5);
                        break;
                    case 5:
                        VigyaanActivity.LinkPDF = CSEPDF;
                        Intent i6 = new Intent(context, ViewPDF.class);
                        context.startActivity(i6);
                        break;
                    case 6:
                        VigyaanActivity.LinkPDF = ElecPDF;
                        Intent i7 = new Intent(context, ViewPDF.class);
                        context.startActivity(i7);
                        break;
                    case 7:
                        VigyaanActivity.LinkPDF = ElexPDF;
                        Intent i8 = new Intent(context, ViewPDF.class);
                        context.startActivity(i8);
                        break;
                    case 8:
                        VigyaanActivity.LinkPDF = ITPDF;
                        Intent i9 = new Intent(context, ViewPDF.class);
                        context.startActivity(i9);
                        break;
                    case 9:
                        VigyaanActivity.LinkPDF = MechPDF;
                        Intent i10 = new Intent(context, ViewPDF.class);
                        context.startActivity(i10);
                        break;
                    case 10:
                        VigyaanActivity.LinkPDF = MetaPDF;
                        Intent i11 = new Intent(context, ViewPDF.class);
                        context.startActivity(i11);
                        break;
                    case 11:
                        VigyaanActivity.LinkPDF = MiningPDF;
                        Intent i12 = new Intent(context, ViewPDF.class);
                        context.startActivity(i12);
                        break;
                    case 12:
                        VigyaanActivity.LinkPDF = MCAPDF;
                        Intent i13 = new Intent(context, ViewPDF.class);
                        context.startActivity(i13);
                        break;
                    case 13:
                        VigyaanActivity.LinkPDF = EcellPDF;
                        Intent i14 = new Intent(context, ViewPDF.class);
                        context.startActivity(i14);
                        break;
                    case 14:
                        VigyaanActivity.LinkPDF = GoGreenPDF;
                        Intent i15 = new Intent(context, ViewPDF.class);
                        context.startActivity(i15);
                        break;
                    default:
                        break;
                }

            }
        });

        return rowView;
    }

}
