<?php
/**
 * Created by PhpStorm.
 * User: fruit
 * Date: 11/27/2018
 * Time: 1:47 PM
 */

namespace App\Http\Controllers;


use App\Permission;

class PermissionController extends Controller
{
    protected $permission;

    public function __construct(Permission $permission)
    {
        parent::__construct($permission, []);
        $this->permission = $permission;
    }
}