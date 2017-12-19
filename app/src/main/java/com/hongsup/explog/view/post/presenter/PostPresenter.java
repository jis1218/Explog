package com.hongsup.explog.view.post.presenter;

import android.util.Log;
import android.widget.Toast;

import com.hongsup.explog.data.post.PostContent;
import com.hongsup.explog.data.post.PostContentResult;
import com.hongsup.explog.data.post.PostCover;
import com.hongsup.explog.data.post.source.PostRepository;
import com.hongsup.explog.view.post.adapter.contract.PostAdapterContract;
import com.hongsup.explog.view.post.contract.PostContract;
import com.hongsup.explog.view.post.listener.OnPostContentClickListener;
import com.hongsup.explog.view.post.listener.OnPostLikeClickListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Android Hong on 2017-12-14.
 */

public class PostPresenter implements PostContract.iPresenter, OnPostContentClickListener, OnPostLikeClickListener {

    private int postPk;
    private PostContract.iView view;
    private PostRepository repository;
    private PostAdapterContract.iView adapterView;
    private PostAdapterContract.iModel adapterModel;

    public PostPresenter() {
        repository = PostRepository.getInstance();
    }

    @Override
    public void attachView(PostContract.iView view) {
        this.view = view;
    }

    @Override
    public void setPostAdapterModel(PostAdapterContract.iModel model) {
        this.adapterModel = model;
    }

    @Override
    public void setPostAdapterView(PostAdapterContract.iView view) {
        this.adapterView = view;
        this.adapterView.setOnPostContentClickListener(this);
        this.adapterView.setOnPostLikeClickListener(this);
    }

    @Override
    public void loadPostContent(PostCover cover) {
        // PK 설정
        this.postPk = cover.getPk();
        view.showProgress();
        Observable<Response<PostContentResult>> observable = repository.getPostContentList(postPk);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                            if (data.isSuccessful()) {
                                Log.e(TAG, "loadPostContent: 데이터 로드 완료");
                                view.hideProgress();

                                if (data.body().getPostContentList() == null || data.body().getPostContentList().size() == 0) {
                                    adapterModel.setInit(cover.getLiked(), cover.getLikeCount(), cover.getAuthor());
                                } else {
                                    adapterModel.setItems(data.body().getPostContentList());
                                    adapterModel.setLikeAndFollow(cover.getLiked(), cover.getLikeCount(), cover.getAuthor());
                                }

                                Log.e(TAG, "loadPostContent: " + cover.getLiked().length);
                                adapterView.notifyAdapter();
                            } else {
                                Log.e(TAG, "loadPostContent: 데이터 로드 실패1");
                                view.hideProgress();
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "loadPostContent: 데이터 로드 실패2");
                            view.hideProgress();
                        });
    }

    @Override
    public void uploadPostText(String text, String date) {
        view.showProgress();
        Observable<Response<PostContent>> observable = repository.uploadPostText(postPk, text, date);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                            Log.e(TAG, "uploadPostText: " + data.code() + ", " + data.message());
                            if (data.isSuccessful()) {
                                Log.e(TAG, "uploadPostText: 데이터 업로드 완료");
                                view.hideProgress();
                                adapterModel.addItems(data.body());
                            } else {
                                Log.e(TAG, "uploadPostText: 데이터 업로드 실패2");
                                view.hideProgress();
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "uploadPostText: 데이터 업로드 실패");
                            view.hideProgress();
                            Log.e(TAG, "uploadPostText: " + throwable.getMessage());
                        });
    }

    @Override
    public void uploadPostPath(double lat, double lng) {
        view.showProgress();
        Observable<Response<PostContent>> observable = repository.uploadPostPath(postPk, lat, lng);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                            Log.e(TAG, "uploadPostPath: " + data.code() + ", " + data.message());
                            if (data.isSuccessful()) {
                                Log.e(TAG, "uploadPostPath: 데이터 업로드 완료");
                                view.hideProgress();
                                adapterModel.addItems(data.body());
                            } else {
                                Log.e(TAG, "uploadPostPath: 데이터 업로드 실패1");
                                view.hideProgress();
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "uploadPostPath: 데이터 업로드 실패2");
                            view.hideProgress();
                            Log.e(TAG, "uploadPostPath: " + throwable.getMessage());
                        });
    }

    @Override
    public void uploadPostPhoto(String photoPath) {
        view.showProgress();
        Observable<Response<PostContent>> observable = repository.uploadPostPhoto(postPk, photoPath);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                            Log.e(TAG, "uploadPostPhoto: " + data.code() + ", " + data.message());
                            if (data.isSuccessful()) {
                                Log.e(TAG, "uploadPostPhoto: 데이터 업로드 완료");
                                view.hideProgress();
                                adapterModel.addItems(data.body());
                            } else {
                                Log.e(TAG, "uploadPostPhoto: 데이터 업로드 실패1");
                                view.hideProgress();
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "uploadPostPhoto: 데이터 업로드 실패2");
                            view.hideProgress();
                            Log.e(TAG, "uploadPostPhoto: " + throwable.getMessage());
                        });
    }

    @Override
    public void setOnLikeClick(int position) {
        Log.e(TAG, "setOnLikeClick: " + position );
        // Toast.makeText(context, "Liked 누름", Toast.LENGTH_SHORT).show();
    }
}
