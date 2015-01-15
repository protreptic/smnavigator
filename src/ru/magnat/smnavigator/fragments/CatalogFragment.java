package ru.magnat.smnavigator.fragments;

import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CatalogFragment extends Fragment {
	
	protected GridLayoutManager mLayoutManager;
	private RecyclerView mRecyclerView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recycler_view, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		sTypeface = Fonts.get(getActivity()).getDefaultTypeface();
		
		initRecyclerView();
	}
	
    protected GridLayoutManager createLayoutManager() {
        GridLayoutManager lm = new GridLayoutManager(getActivity(), 3);
        lm.setReverseLayout(true);
        lm.setSpanSizeLookup(mSpanSizeLookup);
        return lm;
    }
	
    private GridLayoutManager.SpanSizeLookup mSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            Integer item = mAdapter.getValueAt(position);
            return 1 + (Math.abs(item.hashCode()) % mLayoutManager.getSpanCount());
        }
    };
    
    private void initRecyclerView() {
    	mLayoutManager = createLayoutManager();
    	
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(createAdapter());
        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(true);
    }
    
    private PsrAdapter mAdapter;
    
    private PsrAdapter createAdapter() {
        mAdapter = new PsrAdapter();
        
        return mAdapter;
    }
    
    private List<Psr> mPsrs = new ArrayList<Psr>();
    
	public class PsrAdapter extends RecyclerView.Adapter<PsrViewHolder> {
		
		public int getValueAt(int position) {
			return mPsrs.get(position).hashCode();
		}
		
		@Override
		public int getItemCount() {
			return mPsrs.size();
		}

		@Override
		public void onBindViewHolder(PsrViewHolder holder, int position) {
			Psr psr = mPsrs.get(position);
			
			holder.title.setText(psr.getName());
			holder.subtitle.setText(psr.getBranch().getName());
			holder.description.setText(psr.getDepartment().getName()); 
			holder.staticmap.setMappable(psr); 
			holder.staticmaptitle.setText(Text.prepareAddress(psr.getProject()));
		}

		@Override
		public PsrViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_list_item_cardview, parent, false);
			
			return new PsrViewHolder(itemView);
		}
		
	}
	
	public static class PsrViewHolder extends RecyclerView.ViewHolder {

		protected StaticMapView staticmap;
		
		protected TextView title;
		protected TextView subtitle;
		protected TextView description;
		protected TextView staticmaptitle;
		
		public PsrViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			title.setTypeface(sTypeface);  
			
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			subtitle.setTypeface(sTypeface);  
			
			description = (TextView) itemView.findViewById(R.id.description);
			description.setTypeface(sTypeface); 
			
			staticmap = (StaticMapView) itemView.findViewById(R.id.staticmap); 
			
			staticmaptitle = (TextView) itemView.findViewById(R.id.staticmaptitle); 
			staticmaptitle.setTypeface(sTypeface); 
		}

	}
	
	private static Typeface sTypeface;
	
}
