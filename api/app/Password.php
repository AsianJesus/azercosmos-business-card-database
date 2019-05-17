<?php


namespace App;


use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class Password extends Model
{
    protected $fillable = [
        'user_id', 'password', 'salt'
    ];

    protected static function boot()
    {
        parent::boot();

        self::creating(function ($password) {
            $password->{$password->getKeyName()} = Str::uuid();
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