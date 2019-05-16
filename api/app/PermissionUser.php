<?php

namespace App;


use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class PermissionUser extends Model
{
    protected $fillable = [
        'user_id', 'permission_id', 'business_card_id'
    ];
    protected $with = [
        'permission', 'user'
    ];
    protected $table = 'permission_user';

    public function businessCard()
    {
        return $this->belongsTo(BusinessCard::class, 'business_card_id');
    }

    public function user()
    {
        return $this->belongsTo(User::class, 'user_id', 'ID');
    }

    public function permission()
    {
        return $this->belongsTo(Permission::class, 'permission_id');
    }

    protected static function boot()
    {
        parent::boot();

        self::creating(function ($pu) {
            $pu->{$pu->getKeyName} = Str::uuid();
        });
    }

    public function getKeyName()
    {
        return 'id';
    }

    public function getKeyType()
    {
        return 'string';
    }

    public function getIncrementing()
    {
        return false;
    }
}