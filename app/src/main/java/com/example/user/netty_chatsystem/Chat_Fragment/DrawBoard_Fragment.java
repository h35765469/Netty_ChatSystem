package com.example.user.netty_chatsystem.Chat_Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.netty_chatsystem.Chat_CustomeElement.ProgressBar.DrawBoard_drawview;
import com.example.user.netty_chatsystem.R;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class DrawBoard_Fragment extends Fragment {

    protected DrawBoard_drawview mDrawingView;

    public DrawBoard_Fragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.resource_drawboard_fragment, container, false);
        mDrawingView = (DrawBoard_drawview) rootView.findViewById(R.id.drawingview);
        mDrawingView.mCurrentShape = DrawBoard_drawview.SMOOTHLINE;
        mDrawingView.reset();
        return rootView;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_line:
                mDrawingView.mCurrentShape = DrawingView.LINE;
                mDrawingView.reset();
                break;
            case R.id.action_smoothline:
                mDrawingView.mCurrentShape = DrawingView.SMOOTHLINE;
                mDrawingView.reset();
                break;
            case R.id.action_rectangle:
                mDrawingView.mCurrentShape = DrawingView.RECTANGLE;
                mDrawingView.reset();
                break;
            case R.id.action_square:
                mDrawingView.mCurrentShape = DrawingView.SQUARE;
                mDrawingView.reset();
                break;
            case R.id.action_circle:
                mDrawingView.mCurrentShape = DrawingView.CIRCLE;
                mDrawingView.reset();
                break;
            case R.id.action_triangle:
                mDrawingView.mCurrentShape = DrawingView.TRIANGLE;
                mDrawingView.reset();
                break;

        }

        return super.onOptionsItemSelected(item);
    }*/
}