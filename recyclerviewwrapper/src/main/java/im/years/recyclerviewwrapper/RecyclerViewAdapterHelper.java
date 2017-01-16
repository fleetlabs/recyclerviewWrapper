package im.years.recyclerviewwrapper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by alvinzeng on 13/01/2017.
 */

class RecyclerViewAdapterHelper<T> {

    RecyclerViewHelper.SimpleList simpleList;
    RecyclerViewHelper.EasyList easyList;
    RecyclerViewHelper.BriefList briefList;

    RecyclerViewHelper recyclerViewHelper;

    Constructor holderConstructor;

    Constructor viewConstructor;
    Class viewClass;

    Object host;

    public static void attach(Object host, RecyclerViewHelper recyclerViewHelper) {
        new RecyclerViewAdapterHelper(host, recyclerViewHelper);
    }

    private RecyclerViewAdapterHelper(Object host, RecyclerViewHelper recyclerViewHelper) {
        this.host = host;
        if (host instanceof RecyclerViewHelper.SimpleList) {
            simpleList = (RecyclerViewHelper.SimpleList) host;
            try {
                holderConstructor = getActualTypeClass(simpleList.getClass(), 0).getConstructor(new Class[]{View.class});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else if (host instanceof RecyclerViewHelper.EasyList) {
            easyList = (RecyclerViewHelper.EasyList) host;
            try {
                viewClass = getActualTypeClass(simpleList.getClass(), 1);
                viewConstructor = viewClass.getConstructor(new Class[]{Context.class});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else if (host instanceof RecyclerViewHelper.BriefList) {
            briefList = (RecyclerViewHelper.BriefList) host;
        } else {
            Log.d("RecyclerViewHelper", "No interface found");
            return;
        }

        this.recyclerViewHelper = recyclerViewHelper;

        this.recyclerViewHelper.setAdapter(new RecyclerViewAdapter(null));
    }

    public class RecyclerViewAdapter extends BaseQuickAdapter<T, BaseViewHolder> {

        public RecyclerViewAdapter(List<T> data) {
            super(data);
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            if (simpleList != null) {
                simpleList.onBindViewItemHolder(helper, item, recyclerViewHelper.getAdapter().getData().indexOf(item));
            } else if (easyList != null) {
                View view = helper.getConvertView();
                if (view.getClass().getName().equals(viewClass.getName())) {
                    easyList.onBindViewItemHolder(helper, helper.getConvertView(), item, recyclerViewHelper.getAdapter().getData().indexOf(item));
                } else {
                    easyList.onBindViewItemHolder(helper, ((ViewGroup) view).getChildAt(0), item, recyclerViewHelper.getAdapter().getData().indexOf(item));
                }
            } else if (briefList != null) {
                briefList.onBindViewItemHolder(helper, item, recyclerViewHelper.getAdapter().getData().indexOf(item));
            }
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {

            if (simpleList != null) {
                try {

                    View view = getItemView(simpleList.itemViewRes(), parent);

                    if (simpleList.itemViewBackground() > 0) {
                        LinearLayout linearLayout = new LinearLayout(parent.getContext());
                        linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(simpleList.itemViewBackground()));
                        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        linearLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        view = linearLayout;
                    }

                    return (BaseViewHolder) holderConstructor.newInstance(view);
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (easyList != null) {
                return new BaseViewHolder(easyListView());
            } else if (briefList != null) {
                View view = this.mLayoutInflater.inflate(briefList.itemViewRes(), parent, false);
                if (briefList.itemViewBackground() > 0) {
                    LinearLayout linearLayout = new LinearLayout(parent.getContext());
                    linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(briefList.itemViewBackground()));
                    linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    view = linearLayout;
                }
                return new BaseViewHolder(view);
            }

            return super.onCreateDefViewHolder(parent, viewType);
        }
    }

    private View easyListView() {
        View realItemView = null;

        try {
            realItemView = (View) viewConstructor.newInstance(((Fragment) (easyList)).getContext());
            //For androidannotations
            Method method = realItemView.getClass().getMethod("onFinishInflate");
            method.setAccessible(true);
            method.invoke(realItemView);
        } catch (Fragment.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        View view = realItemView;

        if (easyList.itemViewBackground() > 0) {
            LinearLayout linearLayout = new LinearLayout(((Fragment) (easyList)).getContext());
            linearLayout.setBackgroundColor(((Fragment) (easyList)).getContext().getResources().getColor(easyList.itemViewBackground()));
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(realItemView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            view = linearLayout;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return view;
    }

    private Class getActualTypeClass(Class entity, int index) {
        Type listInterface = (entity.getGenericInterfaces() == null || entity.getGenericInterfaces().length == 0) ? null : getActualTypeFromGenericInterfaces(entity.getGenericInterfaces());
        boolean find = (listInterface != null && listInterface instanceof ParameterizedType);
        while (!find) {
            entity = (Class) entity.getGenericSuperclass();
            listInterface = getActualTypeFromGenericInterfaces(entity.getGenericInterfaces());
            find = (listInterface != null && listInterface instanceof ParameterizedType);
        }

        ParameterizedType type = (ParameterizedType) listInterface;
        Class entityClass = (Class) type.getActualTypeArguments()[index];
        return entityClass;
    }

    private ParameterizedType getActualTypeFromGenericInterfaces(Type[] genericInterfaces) {
        if (genericInterfaces == null) {
            return null;
        }
        ParameterizedType clss = null;
        for (int i = 0; i < genericInterfaces.length; i++) {
            Type cls = genericInterfaces[i];
            clss = (ParameterizedType) cls;
            if (((Class) clss.getRawType()).isInstance(host)) {
                break;
            }
        }

        return clss;
    }

}
