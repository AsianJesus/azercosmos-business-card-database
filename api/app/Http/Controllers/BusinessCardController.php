<?php

namespace App\Http\Controllers;


use App\BusinessCard;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Str;

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
        Log::debug(json_encode($request->all()));
        Log::debug(json_encode($request->file('photo')));
        Log::debug(date('Y-m-d H:i:s'));
        if ($request->file('photo')) {
            $photo = $request->file('photo');
            $name = time() . '.' . $photo->extension();
            $photo->move('images/', $name);
            $request['image_path'] = 'images/' . $name;
        }
        $request['created_by'] = app()->id;
        $card = parent::add($request);
        if ($request->input('note')) {
            $card->notes()->create(['note' => $request->input('note')]);
        }
        foreach($request->input('permissions', []) ?? [] as $user_id => $permissions) {
            foreach($permissions as $permission) {
                $card->permissions()->create([
                    'user_id' => $user_id,
                    'permission_id' => $permission
                ]);
            }
        }
        return $this->getById($card->id);
    }

    public function getAll(Request $request)
    {
        $query = $this->business_card::query();
        if ($request->input('search')) {
            $pattern = $request->input('search');
            $query->where(function ($where) use ($pattern) {
                $where->where('name', 'LIKE', "%$pattern%")->orWhere('company_name', 'LIKE', "%$pattern%");
            });
        }
        if ($request->input('filters')) {
            $filters = $request->input('filters');
            if (is_string($filters)) {
                $filters = (array)json_decode($filters);
            }
            foreach (array_keys($filters) as $key) {
                $query->where($key, 'like', $filters[$key]);
            }
        }
        $query->with('permissions.permission', 'permissions.user')->orderBy('created_at', 'desc');
        return $request->input('paginate', false) ? $query->paginate($request->input('amount', 10))
            : $query->get();
    }

    public function getOne(Request $request, $id)
    {
        $query = $this->business_card::query();
        if ($request->input('filters')) {
            $filters = $request->input('filters');
            if (is_string($filters)) {
                $filters = (array)json_decode($filters);
            }
            foreach (array_keys($filters) as $key) {
                $query->where($key, $filters[$key]);
            }
        }
        return $query->with('permissions.permission', 'permissions.user')->where('id',$id)->orderBy('created_at', 'desc')->get();
    }


    public function exportExcel(Request $request)
    {
        $query = $this->business_card::query();
        if ($request->input('filters')) {
            $filters = $request->input('filters');
            if (is_string($filters)) {
                $filters = (array)json_decode($filters);
            }
            foreach (array_keys($filters) as $key) {
                $query->where($key, $filters[$key]);
            }
        }
        return $query->with('permissions.permission', 'permissions.user')->orderBy('created_at', 'desc')->get();
        foreach ($query as $row) {
            $query["note"] = isset($row->notes[0]->note) ? $row->notes[0]->note : " ";
        }
        return $query;
    }

    public function deletePermissionOfUser(Request $request, $id)
    {
        $user_id = $request->input('user_id');
        $business_card = $this->business_card::findOrFail($id);
        $business_card->permissions()->where('user_id', $user_id)->delete();
        return $business_card->permissions()->get();
    }

    public function update(Request $request, $id)
    {
//        $this->validate($request, $this->rules);
//        return $request->all();
        Log::debug(json_encode($request->all()));
        Log::debug($request->file('photo'));

        if ($request->file('photo')) {
            $photo = $request->file('photo');
            $name = time() . '.' . $photo->extension();
            $photo->move('images/', $name);
            $request['image_path'] = 'images/' . $name;
        }

        $update = $this->business_card::findOrFail($id);
        $result = $update->fill($request->all())->save();
        Log::debug(json_encode($update));
        $note = DB::table("bcard_notes")->where("business_card_id", $id)->first();
        if ($note != null) {
            DB::table("bcard_notes")->where("business_card_id", $id)->update([
                "note" => $request->note ?? ''
            ]);
        } else {
            if ($request->note != null) {
                DB::table("bcard_notes")->where("business_card_id", $id)->insert([
                    "note" => $request->note,
                    "business_card_id" => $id,
                    'id' => Str::uuid()
                ]);
            }
        }
        return response()->json($update);
    }

    function doesExists(Request $request) {
        $name = $request->input('name');
        $company_name = $request->input('company_name');
        $position = $request->input('position');
        $user_id = app()->id;
        return json_encode(
          $this->business_card::where('name', $name)
              ->where('company_name', $company_name)
              ->where('position', $position)->where(function ($w) use ($user_id) {
                  $w->where('private', false)->orWhere('created_by', $user_id);
              })->first() != null
        );
    }

}
