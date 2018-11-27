<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use Laravel\Lumen\Routing\Controller as BaseController;

class Controller extends BaseController
{
    private $model;
    private $rules;

    public function __construct($model, $rules)
    {
        $this->model = $model;
        $this->rules = $rules;
    }

    public function add(Request $request)
    {
//        $this->validate($request, $this->rules);
        $lastInsertedRow = $this->model::create($request->all());
        return $lastInsertedRow;
    }

    public function update(Request $request, $id)
    {
//        $this->validate($request, $this->rules);
        $update = $this->model::findOrFail($id);
        $result = $update->fill($request->all())->save();
        return response()->json($update);
    }

    public function delete(Request $request, $id)
    {
        $deleted = $this->model::destroy($id);
        return $deleted;
    }

    public function getAll()
    {
        return $this->model::all();
    }

    public function getById($id)
    {
        return $this->model::where('id', $id)->firstOrFail();
    }
}
