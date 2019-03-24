<?php

namespace App\Http\Controllers;


use App\BusinessCard;
use Illuminate\Http\Request;

class BusinessCardController extends Controller
{
    protected $business_card;
    protected $rules = [];

    public function __construct(BusinessCard $card)
    {
        parent::__construct($card, $this->rules);
        $this->business_card = $card;
    }
    public function add(Request $request)
    {
        if ($request->file('photo')) {
            $photo = $request->file('photo');
            $name = time().'.'.$photo->extension();
            $photo->move('images/',$name);
            $request['image_path'] = 'images/'.$name;
        }
        $request['created_by'] = app()->id;
        return parent::add($request);
    }
    public function getAll(Request $request)
    {
        $query = $this->business_card::query();
        if ($request->input('filters')) {
            $filters = $request->input('filters');
            if (is_string($filters)){
                $filters = (array)json_decode($filters);
            }
            foreach (array_keys($filters) as $key) {
                $query->where($key, $filters[$key]);
            }
        }
        return $query->with('permissions.permission', 'permissions.user')->orderBy('created_at', 'desc')->get();
    }
    public function deletePermissionOfUser(Request $request, $id) {
        $user_id = $request->input('user_id');
        $business_card = $this->business_card::findOrFail($id);
        $business_card->permissions()->where('user_id', $user_id)->delete();
        return $business_card->permissions()->get();
    }
}